package org.wisdom.db;

import org.tdf.common.util.ChainCache;
import org.wisdom.core.Block;
import org.wisdom.genesis.Genesis;

import java.util.*;
import java.util.stream.Collectors;

public abstract class EraLinkedStateTrie<T> extends StateTrieAdapter<T>{
    public EraLinkedStateTrie(
            Block genesis, Genesis genesisJSON, Class<T> clazz, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset) {
        super(genesis, genesisJSON, clazz, factory, logDeletes, reset);
    }

    abstract protected int getBlocksPerEra();

    protected abstract Map<byte[], T> getUpdatedStates(Map<byte[], T> beforeUpdates, Collection<Block> blocks);
    protected abstract Set<byte[]> getRelatedKeys(Collection<Block> blocks);

    public static long getEraAtBlockNumber(long number, int blocksPerEra) {
        return (number - 1) / blocksPerEra;
    }

    private Block prevEraLast(Block target) {
        if (target.nHeight == 0) {
            throw new RuntimeException("cannot find prev era last of genesis");
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight, getBlocksPerEra()) * getBlocksPerEra();
        if (lastHeaderNumber == target.nHeight - 1) {
            return getChain().getHeader(target.hashPrevBlock);
        }
        return getChain().findAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }

    @Override
    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash) {
        Block b = getChain().getBlock(blockHash);
        Block prevEraLast = prevEraLast(b);
        return super.get(prevEraLast.getHash(), publicKeyHash);
    }

    @Override
    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Block b = getChain().getBlock(blockHash);
        Block prevEraLast = prevEraLast(b);
        return super.batchGet(prevEraLast.getHash(), keys);
    }

    @Override
    public void commit(List<Block> blocks) {
        if(blocks.size() != getBlocksPerEra()) throw new RuntimeException("not an era size = " + blocks.size());
        Block last = blocks.get(blocks.size() - 1);
        if(last.nHeight % getBlocksPerEra() != 0)
            throw new RuntimeException("not an era from " + blocks.get(0).nHeight + " to " + last.nHeight);

        if(getRootStore().containsKey(last.getHash())) return;
        Set<byte[]> keys = getRelatedKeys(blocks);
        Map<byte[], T> updated = getUpdatedStates(batchGet(blocks.get(0).hashPrevBlock, keys), blocks);

        commitInternal(
                getRootStore().get(blocks.get(0).hashPrevBlock)
                        .orElseThrow(() -> new RuntimeException("unreachable")),
                last.getHash(),
                updated
        );
    }

    @Override
    public void commit(Block block) {
        if(block.nHeight % getBlocksPerEra() != 0){
            return;
        }
        if(getRootStore().containsKey(block.getHash())) return;
        List<Block> ancestors = getChain().getAncestorBlocks(block.getHash(), getBlocksPerEra());
        commit(ancestors);
    }
}
