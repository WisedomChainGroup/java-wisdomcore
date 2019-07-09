package org.wisdom.consensus.pow;

import org.apache.commons.math3.fraction.BigFraction;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.wisdom.core.state.State;
import org.wisdom.core.account.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

/**
 * @author sal 1564319846@qq.com
 * adjust difficulty per era
 */
@Component
public class TargetState implements State {
    private int blockInterval;
    private static final Logger logger = LoggerFactory.getLogger(TargetState.class);
    private static final long MAX_ADJUST_RATE = 16;

    private BigInteger target;

    public TargetState() {
    }

    @Autowired
    public TargetState(Block genesis, @Value("${wisdom.consensus.block-interval}") int blockInterval) {
        this.target = BigEndian.decodeUint256(genesis.nBits);
        this.blockInterval = blockInterval;
    }

    @Override
    public State updateBlock(Block block) {
        return this;
    }

    // TODO: add minRetargetTimespan & maxRetargetTimespan

    /**
     * Calculate new target difficulty as:
     * currentDifficulty * (adjustedTimespan / targetTimespan)
     * The result uses integer division which means it will be slightly
     * rounded down.  Bitcoind also uses integer division to calculate this
     * result.
     *
     * @param blocks
     * @return
     */
    @Override
    public State updateBlocks(List<Block> blocks) {
        long actualTimeSpent = blocks.get(blocks.size() - 1).nTime - blocks.get(0).nTime;
        if (actualTimeSpent <= 0) {
            target = target.divide(BigInteger.valueOf(MAX_ADJUST_RATE));
            return this;
        }
        BigFraction rate = new BigFraction(actualTimeSpent, (long) blockInterval * (blocks.size() - 1));
        if (rate.compareTo(new BigFraction(MAX_ADJUST_RATE)) > 0) {
            rate = new BigFraction(MAX_ADJUST_RATE);
        }
        if (rate.compareTo(new BigFraction(1, MAX_ADJUST_RATE)) < 0) {
            rate = new BigFraction(1, MAX_ADJUST_RATE);
        }
        logger.info("adjust rate = " + rate.doubleValue());
        target = safeTyMul(target, rate);
        return this;
    }

    private BigInteger safeTyMul(BigInteger target, BigFraction f){
        target = target.multiply(f.getNumerator());
        if (target.compareTo(BigEndian.MAX_UINT_256) > 0){
            target = BigEndian.MAX_UINT_256;
        }
        return target.divide(f.getDenominator());
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        return this;
    }

    @Override
    public State copy() {
        TargetState d = new TargetState();
        d.target = target;
        d.blockInterval = blockInterval;
        return d;
    }

    public BigInteger getTarget() {
        return target;
    }
}
