package org.wisdom.db;

import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractStateUpdater<T> {
    abstract Map<byte[], T> getGenesisStates();

    abstract Set<byte[]> getRelatedKeys(Transaction transaction);

    Set<byte[]> getRelatedKeys(Block block) {
        Set<byte[]> set = new ByteArraySet();
        block.body.forEach(tx -> {
            set.addAll(this.getRelatedKeys(tx));
        });
        return set;
    }

    Set<byte[]> getRelatedKeys(List<Block> block) {
        Set<byte[]> set = new ByteArraySet();
        block.forEach(b -> {
            set.addAll(getRelatedKeys(b));
        });
        return set;
    }

    // the update method should always returns a new state
    abstract T update(byte[] id, T state, Block block, Transaction transaction);

    abstract T createEmpty(byte[] id);

    public Map<byte[], T> update(Map<byte[], T> beforeUpdate, Block block) {
        Map<byte[], T> ret = new ByteArrayMap<>(beforeUpdate);
        block.body.forEach(tx -> {
            getRelatedKeys(tx).forEach(k -> {
                ret.put(k, update(k, ret.get(k), block, tx));
            });
        });
        return ret;
    }

    public Map<byte[], T> update(Map<byte[], T> beforeUpdate, List<Block> blocks) {
        Map<byte[], T> ret = new ByteArrayMap<>(beforeUpdate);
        blocks.forEach(b -> {
                    b.body.forEach(tx -> {
                        getRelatedKeys(tx).forEach(k -> {
                            ret.put(k, update(k, ret.get(k), b, tx));
                        });
                    });
                });
        return ret;
    }
}
