package org.wisdom.db;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.math3.fraction.BigFraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.tdf.common.util.HexBytes;
import org.wisdom.Start;
import org.wisdom.core.Block;
import org.wisdom.encoding.BigEndian;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class TargetCache {
    private static final long MAX_ADJUST_RATE = 16;


    private int initialBlockInterval;
    private long blockIntervalSwitchEra;
    private int blockIntervalSwitchTo;
    private byte[] genesisTarget;

    private EraLinker eraLinker;
    private WisdomRepository repository;

    public void setRepository(WisdomRepository repository) {
        this.repository = repository;
        eraLinker.setRepository(repository);
    }

    public TargetCache(
            Block genesis,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
            @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        this.initialBlockInterval = blockInterval;
        this.blockIntervalSwitchEra = blockIntervalSwitchEra;
        this.blockIntervalSwitchTo = blockIntervalSwitchTo;
        this.genesisTarget = genesis.nBits;
        this.eraLinker = new EraLinker(blocksPerEra);
    }

    public byte[] getTargetAt(Block parentBlock) {
        if (parentBlock.nHeight < eraLinker.getBlocksPerEra()) return genesisTarget;
        if(parentBlock.nHeight % eraLinker.getBlocksPerEra() != 0) return parentBlock.nBits;
        List<Block> updates = repository
                .getAncestorBlocks(parentBlock.getHash(), parentBlock.nHeight - eraLinker.getBlocksPerEra() + 1);
        return updateBlocks(parentBlock.nBits, updates);
    }

    private byte[] updateBlocks(byte[] nBits, List<Block> blocks) {
        long blockInterval = this.initialBlockInterval;

        if (blockIntervalSwitchEra >= 0 && eraLinker.getEraAtBlockNumber(blocks.get(0).nHeight) >= blockIntervalSwitchEra) {
            blockInterval = blockIntervalSwitchTo;
        }

        BigInteger target = BigEndian.decodeUint256(nBits);

        long actualTimeSpent = blocks.get(blocks.size() - 1).nTime - blocks.get(0).nTime;
        if (actualTimeSpent <= 0) {
            return BigEndian.encodeUint256(target.divide(BigInteger.valueOf(MAX_ADJUST_RATE)));
        }
        BigFraction rate = new BigFraction(actualTimeSpent, blockInterval * (blocks.size() - 1));
        if (rate.compareTo(new BigFraction(MAX_ADJUST_RATE)) > 0) {
            rate = new BigFraction(MAX_ADJUST_RATE);
        }
        if (rate.compareTo(new BigFraction(1, MAX_ADJUST_RATE)) < 0) {
            rate = new BigFraction(1, MAX_ADJUST_RATE);
        }
        if (Start.ENABLE_ASSERTION) {
            Assert.isTrue(Arrays.equals(BigEndian.encodeUint256(target), blocks.get(0).nBits), "target unmatched");
        }
        return BigEndian.encodeUint256(safeTyMul(target, rate));
        // logger.info("update blocks start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight + " target = " + Hex.encodeHexString(BigEndian.encodeUint256(target)));
    }

    private BigInteger safeTyMul(BigInteger target, BigFraction f) {
        target = target.multiply(f.getNumerator());
        if (target.compareTo(BigEndian.MAX_UINT_256) > 0) {
            target = BigEndian.MAX_UINT_256;
        }
        return target.divide(f.getDenominator());
    }
}
