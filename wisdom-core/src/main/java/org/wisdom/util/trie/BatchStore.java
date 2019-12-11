package org.wisdom.util.trie;

import java.util.Map;

public interface BatchStore<K, V> extends Store<K, V> {
    void putAll(Map<K, V> rows);
}