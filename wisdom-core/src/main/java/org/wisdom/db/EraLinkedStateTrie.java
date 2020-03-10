package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.core.Block;

import java.util.*;

public abstract class EraLinkedStateTrie<T> extends StateTrieAdapter<T> {
    public EraLinkedStateTrie(
            Class<T> clazz, AbstractStateUpdater<T> updater, Block genesis,
            DatabaseStoreFactory factory, boolean logDeletes, boolean reset, int blocksPerEra) {
        super(clazz, updater, genesis, factory, logDeletes, reset);
        this.eraLinker = new EraLinker(blocksPerEra);
    }

    @Getter(AccessLevel.PROTECTED)
    private WisdomRepository repository;

    protected void setRepository(WisdomRepository repository) {
        this.repository = repository;
        this.eraLinker.setRepository(repository);
    }

    abstract void updateHook(Block eraLast, Trie<byte[], T> trie);

    protected EraLinker eraLinker;

    @Override
    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash) {
        Block b = getRepository().getHeaderByHash(blockHash);
        Block prevEraLast = eraLinker.getPrevEraLast(b);
        return super.get(prevEraLast.getHash(), publicKeyHash);
    }

    @Override
    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Block b = getRepository().getHeaderByHash(blockHash);
        Block prevEraLast = eraLinker.getPrevEraLast(b);
        return super.batchGet(prevEraLast.getHash(), keys);
    }

    protected void commitInternal(List<Block> blocks) {
        Set<byte[]> size = new ByteArraySet();
        blocks.forEach(b -> size.add(b.getHash()));
        if (size.size() != eraLinker.getBlocksPerEra())
            throw new RuntimeException("not an era size = " + blocks.size());
        
        Block last = blocks.get(blocks.size() - 1);

        if (last.nHeight % eraLinker.getBlocksPerEra() != 0 || blocks.get(0).nHeight % eraLinker.getBlocksPerEra() != 1)
            throw new RuntimeException("not an era from " + blocks.get(0).nHeight + " to " + last.nHeight);

        Optional<byte[]> root = getRootStore().get(last.getHash());
        if (root.isPresent()) {
            updateHook(last, getTrieByBlockHash(last.getHash()));
            return;
        }

        Trie<byte[], T> prevTrie = getTrieByBlockHash(blocks.get(0).hashPrevBlock);

        Set<byte[]> keys = getUpdater().getRelatedKeys(blocks, prevTrie.asMap());
        Map<byte[], T> beforeUpdate = new ByteArrayMap<>();
        keys.forEach(k -> beforeUpdate.put(k, prevTrie.get(k).orElse(getUpdater().createEmpty(k))));
        Map<byte[], T> updated = getUpdater().update(beforeUpdate, blocks);

        Trie<byte[], T> after = super.commitInternal(
                getRootStore().get(blocks.get(0).hashPrevBlock)
                        .orElseThrow(() -> new RuntimeException("unreachable")),
                last.getHash(),
                updated
        );
        updateHook(blocks.get(blocks.size() - 1), after);
    }

    @Override
    public byte[] commit(Block block) {
        if (block.nHeight % eraLinker.getBlocksPerEra() != 0) {
            return new byte[0];
        }
        Optional<byte[]> root = getRootStore().get(block.getHash());
        if (root.isPresent()) {
            updateHook(block, getTrie().revert(root.get()));
            return new byte[0];
        }
        List<Block> ancestors = getRepository().getAncestorBlocks(
                block.getHash(),
                block.nHeight - eraLinker.getBlocksPerEra() + 1
        );
        commit(ancestors);
        return new byte[0];
    }

    @Override
    public void commit(List<Block> blocks) {
        commitInternal(blocks);
    }
}
