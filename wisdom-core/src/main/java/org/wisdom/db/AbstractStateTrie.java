package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.Block;

import java.util.Map;

public abstract class AbstractStateTrie<T> extends StateTrieAdapter<T>{
    public AbstractStateTrie(
            Class<T> clazz, AbstractStateUpdater<T> updater, Block genesis, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset
    ) {
        super(clazz, updater, genesis, factory, logDeletes, reset);
    }


    // TODO: avoid root hash conflicts
    @Override
    public void commit(Block block) {
        if(block.nHeight == 0) throw new RuntimeException("cannot commit genesis block");
        if (getRootStore().containsKey(block.getHash()))
            return;
        byte[] root = getRootStore().get(block.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(block.hashPrevBlock) + " not exists"));
        Map<byte[], T> beforeUpdates = batchGet(block.hashPrevBlock, getUpdater().getRelatedKeys(block));
        commitInternal(root, block.getHash(), getUpdater().update(beforeUpdates, block));
    }
}
