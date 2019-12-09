package org.wisdom.db;

import org.wisdom.core.Block;

public interface AccountDB {
    AccountState getAccount(byte[] blockHash, byte[] publicKeyHash);
    void commit(Block block);
}
