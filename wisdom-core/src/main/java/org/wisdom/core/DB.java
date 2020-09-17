package org.wisdom.core;

import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.wisdom.db.AccountState;

import java.util.Map;

public interface DB {
    Map<byte[], AccountState> getAccountStore();
    Trie<byte[], byte[]> getStorageTrie();
    Store<byte[], byte[]>  getContractCodeStore();
}
