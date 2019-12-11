package org.wisdom.util.trie;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ReadOnlyStore<K, V> implements Store<K, V>{
    private Store<K, V> delegate;

    public ReadOnlyStore(Store<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<V> get(K k) {
        return delegate.get(k);
    }

    @Override
    public void put(K k, V v) {
        throw new RuntimeException("the store is read only");
    }

    @Override
    public void putIfAbsent(K k, V v) {
        throw new RuntimeException("the store is read only");
    }

    @Override
    public void remove(K k) {
        throw new RuntimeException("the store is read only");
    }

    @Override
    public boolean flush() {
        throw new RuntimeException("the store is read only");
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public boolean containsKey(K k) {
        return delegate.containsKey(k);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void clear() {
        throw new RuntimeException("the store is read only");
    }
}

