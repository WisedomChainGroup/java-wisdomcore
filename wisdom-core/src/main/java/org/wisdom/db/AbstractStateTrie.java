package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.tdf.common.store.MemoryCachedStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.wisdom.core.Block;
import org.wisdom.genesis.Genesis;

import java.util.Map;
import java.util.Set;

public abstract class AbstractStateTrie<T> extends StateTrieAdapter<T>{
    protected abstract Map<byte[], T> getUpdatedStates(Map<byte[], T> beforeUpdates, Block block);
    protected abstract Set<byte[]> getRelatedKeys(Block block);

    public AbstractStateTrie(
            Block genesis, Genesis genesisJSON, Class<T> clazz, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset
    ) {
        super(genesis, genesisJSON, clazz, factory, logDeletes, reset);
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
