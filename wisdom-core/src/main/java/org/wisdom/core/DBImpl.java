package org.wisdom.core;

import lombok.Value;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.wisdom.db.AccountState;

import java.util.Map;


@Value
public class DBImpl implements DB{
    Trie<byte[], AccountState> accountStateTrie;
    Trie<byte[], byte[]> storageTrie;
    Store<byte[], byte[]> contractCodeStore;

    @Override
    public Map<byte[], AccountState> getAccountStore() {
        return accountStateTrie.asMap();
    }
}
