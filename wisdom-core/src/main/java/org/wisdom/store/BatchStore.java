package org.wisdom.store;

import java.util.Map;

public interface BatchStore<K, V> extends Store<K, V> {
    void putAll(Map<K, V> rows);
}