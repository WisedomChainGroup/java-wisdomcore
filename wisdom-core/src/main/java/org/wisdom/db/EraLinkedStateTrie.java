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
    public void commit(Block block) {
        if(block.nHeight % getBlocksPerEra() != 0){
            return;
        }
        if(getRootStore().containsKey(block.getHash())) return;
        Block prevEraLast = prevEraLast(block);
        List<Block> ancestors = getChain().getAncestorBlocks(block.getHash(), getBlocksPerEra());
        Set<byte[]> keys = getRelatedKeys(ancestors);
        Map<byte[], T> updated = getUpdatedStates(batchGet(block.getHash(), keys), ancestors);

        commitInternal(
                getRootStore().get(prevEraLast.getHash())
                .orElseThrow(() -> new RuntimeException("unreachable")),
                block.getHash(),
                updated
        );
    }
}
