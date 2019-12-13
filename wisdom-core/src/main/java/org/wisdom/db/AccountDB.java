package org.wisdom.db;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface AccountDB {
    Optional<AccountState> getAccount(byte[] blockHash, byte[] publicKeyHash);
    Map<byte[], AccountState> getAccounts(byte[] blockHash, Collection<byte[]> publicKeyHashes);
    byte[] putAccounts(byte[] parentHash, byte[] blockHash, Collection<AccountState> accounts);
}
