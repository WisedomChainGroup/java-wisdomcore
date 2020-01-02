package org.wisdom.db;

import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.wisdom.core.Block;
import org.wisdom.genesis.Genesis;

import java.util.*;

public abstract class EraLinkedStateTrie<T> extends StateTrieAdapter<T>{
    public EraLinkedStateTrie(
            Block genesis, Genesis genesisJSON, Class<T> clazz, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset) {
        super(genesis, genesisJSON, clazz, factory, logDeletes, reset);
    }

    abstract protected int getBlocksPerEra();

    protected abstract Map<byte[], T> getUpdatedStates(Map<byte[], T> beforeUpdates, List<Block> blocks);
    protected abstract Set<byte[]> getRelatedKeys(List<Block> blocks);

    protected long getEraAtBlockNumber(long number) {
        return (number - 1) / getBlocksPerEra();
    }

    protected Block prevEraLast(Block target) {
        if (target.nHeight == 0) {
            throw new RuntimeException("cannot find prev era last of genesis");
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight) * getBlocksPerEra();
        if (lastHeaderNumber == target.nHeight - 1) {
            return getRepository().getHeader(target.hashPrevBlock);
        }
        return getRepository().findAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }

    @Override
    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash) {
        Block b = getRepository().getBlock(blockHash);
        Block prevEraLast = prevEraLast(b);
        return super.get(prevEraLast.getHash(), publicKeyHash);
    }

    @Override
    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Block b = getRepository().getBlock(blockHash);
        Block prevEraLast = prevEraLast(b);
        return super.batchGet(prevEraLast.getHash(), keys);
    }

    protected Trie<byte[], T> commitInternal(List<Block> blocks) {
        if(blocks.size() != getBlocksPerEra()) throw new RuntimeException("not an era size = " + blocks.size());
        Block last = blocks.get(blocks.size() - 1);
        if(last.nHeight % getBlocksPerEra() != 0)
            throw new RuntimeException("not an era from " + blocks.get(0).nHeight + " to " + last.nHeight);

        if(getRootStore().containsKey(last.getHash())) throw new RuntimeException("has commit");
        Trie<byte[], T> prevTrie = getRootStore()
                .get(blocks.get(0).hashPrevBlock)
                .map(getTrie()::revert)
                .orElseThrow(() -> new RuntimeException("not synced"))
                ;
        Set<byte[]> keys = getRelatedKeys(blocks);
        Map<byte[], T> beforeUpdate = new ByteArrayMap<>();
        keys.forEach(k -> beforeUpdate.put(k, prevTrie.get(k).orElse(createEmpty(k))));
        Map<byte[], T> updated = getUpdatedStates(beforeUpdate, blocks);

        return commitInternal(
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
        List<Block> ancestors = getRepository().getAncestorBlocks(block.getHash(), getBlocksPerEra());
        commit(ancestors);
    }
}
