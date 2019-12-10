package org.wisdom.util.trie;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Store<K, V> {
    Optional<V> get(K k);

    void put(K k, V v);

    void putIfAbsent(K k, V v);

    void remove(K k);

    Set<K> keySet();

    Collection<V> values();

    boolean containsKey(K k);

    int size();

    boolean isEmpty();

    void clear();
}
