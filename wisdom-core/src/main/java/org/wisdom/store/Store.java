package org.wisdom.store;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface Store<K, V> {

    /**
     * Gets a value by its key
     * @return value or empty if no such key in the source
     */
    Optional<V> get(K k);

    /**
     * Puts key-value pair into store
     */
    void put(K k, V v);

    void putIfAbsent(K k, V v);

    /**
     * Deletes the key-value pair from the source
     */
    void remove(K k);

    /**
     * If this source has underlying level source then all
     * changes collected in this source are flushed into the
     * underlying source.
     * The implementation may do 'cascading' flush, i.e. call
     * flush() on the underlying Source
     */
    void flush();

    Set<K> keySet();

    Collection<V> values();

    boolean containsKey(K k);

    int size();

    boolean isEmpty();

    void clear();
}
