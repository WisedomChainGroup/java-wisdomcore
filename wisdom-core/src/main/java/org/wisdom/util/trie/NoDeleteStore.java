package org.wisdom.util.trie;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class NoDeleteStore<K, V> implements Store<K, V>{
    private Store<K, V> delegate;

    private Store<K, V> deleted;

    @Override
    public Optional<V> get(@NonNull K k) {
        return delegate.get(k);
    }

    @Override
    public void put(@NonNull K k, @NonNull V v) {
        deleted.remove(k);
        delegate.put(k, v);
    }

    @Override
    public void putIfAbsent(@NonNull K k, @NonNull V v) {
        delegate.putIfAbsent(k, v);
    }

    @Override
    public void remove(@NonNull K k) {
        Optional<V> o = get(k);
        if(!o.isPresent()) return;
        // we not remove k, just add it to a cache
        // when flush() called, we remove k in the cache
        deleted.put(k, o.get());
    }

    // flush all deleted to underlying db
    @Override
    public void flush() {
        deleted.flush();
        delegate.flush();
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
    public boolean containsKey(@NonNull K k) {
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
        deleted.clear();
        delegate.clear();
    }
}
