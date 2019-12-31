package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.tdf.common.store.MemoryCachedStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.wisdom.core.Block;

import java.util.Map;
import java.util.Set;

public abstract class AbstractStateTrie<T> extends StateTrieAdapter<T>{
    protected abstract Map<byte[], T> getUpdatedStates(Map<byte[], T> beforeUpdates, Block block);
    protected abstract Set<byte[]> getRelatedKeys(Block block);

    public AbstractStateTrie(
            Block genesis, Class<T> clazz, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset
    ) {
        super(genesis, clazz, factory, logDeletes, reset);
    }

    private byte[] commitInternal(byte[] root, byte[] blockHash, Map<byte[], T> data){
        Store<byte[], byte[]> cache = new MemoryCachedStore<>(getTrieStore());
        Trie<byte[], T> trie = getTrie().revert(root, cache);
        for (Map.Entry<byte[], T> entry: data.entrySet()) {
            trie.put(entry.getKey(), entry.getValue());
        }
        byte[] newRoot = trie.commit();
        trie.flush();
        getRootStore().put(blockHash, newRoot);
        return newRoot;
    }

    @Override
    public void commit(Block block) {
        if(block.nHeight == 0) throw new RuntimeException("cannot commit genesis block");
        if (getRootStore().containsKey(block.getHash()))
            return;
        byte[] root = getRootStore().get(block.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(block.hashPrevBlock) + " not exists"));
        Map<byte[], T> beforeUpdates = batchGet(block.hashPrevBlock, getRelatedKeys(block));
        commitInternal(root, block.getHash(), getUpdatedStates(beforeUpdates, block));
    }
}
