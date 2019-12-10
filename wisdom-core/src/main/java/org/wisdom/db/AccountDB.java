package org.wisdom.db;

import org.wisdom.core.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface AccountDB {
    Optional<AccountState> getAccount(byte[] blockHash, byte[] publicKeyHash);
    Map<byte[], AccountState> getAccounts(byte[] blockHash, Collection<byte[]> publicKeyHashes);
    byte[] putAccounts(byte[] blockHash, Collection<AccountState> accounts);
    void confirm(Block block, Collection<AccountState> confirmed);
}
