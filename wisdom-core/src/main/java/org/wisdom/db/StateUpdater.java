package org.wisdom.db;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StateUpdater<T> {
    Map<byte[], T> getGenesisStates();

    Set<byte[]> getRelatedKeys(Transaction transaction);

    T createEmpty(byte[] id);

    // the update method should always returns a new state
    T update(byte[] id, T state, TransactionInfo info);

    Map<byte[], T> update(Map<byte[], T> beforeUpdate, Collection<? extends TransactionInfo> transactionInfos);

    Map<byte[], T> update(Map<byte[], T> beforeUpdate, Block block);

    Map<byte[], T> update(Map<byte[], T> beforeUpdate, List<Block> blocks);
}
