package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
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
        Block b = getRepository().getBlockByHash(blockHash);
        Block prevEraLast = eraLinker.getPrevEraLast(b);
        return super.get(prevEraLast.getHash(), publicKeyHash);
    }

    @Override
    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Block b = getRepository().getBlockByHash(blockHash);
        Block prevEraLast = eraLinker.getPrevEraLast(b);
        return super.batchGet(prevEraLast.getHash(), keys);
    }

    protected void commitInternal(List<Block> blocks) {
        if (blocks.size() != eraLinker.getBlocksPerEra())
            throw new RuntimeException("not an era size = " + blocks.size());
        Block last = blocks.get(blocks.size() - 1);
        if (last.nHeight % eraLinker.getBlocksPerEra() != 0)
            throw new RuntimeException("not an era from " + blocks.get(0).nHeight + " to " + last.nHeight);

        Optional<byte[]> root = getRootStore().get(last.getHash());
        if (root.isPresent()) {
            updateHook(last, getTrie().revert(root.get()));
            return;
        }

        Trie<byte[], T> prevTrie = getRootStore()
                .get(blocks.get(0).hashPrevBlock)
                .map(getTrie()::revert)
                .orElseThrow(() -> new RuntimeException("not synced"));

        Set<byte[]> keys = getUpdater().getRelatedKeys(blocks);
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
    public void commit(Block block) {
        if (block.nHeight % eraLinker.getBlocksPerEra() != 0) {
            return;
        }
        Optional<byte[]> root = getRootStore().get(block.getHash());
        if (root.isPresent()) {
            updateHook(block, getTrie().revert(root.get()));
            return;
        }
        List<Block> ancestors = getRepository().getAncestorBlocks(
                block.getHash(),
                block.nHeight - eraLinker.getBlocksPerEra() + 1
        );
        commit(ancestors);
    }

    @Override
    public void commit(List<Block> blocks) {
        commitInternal(blocks);
    }
}
