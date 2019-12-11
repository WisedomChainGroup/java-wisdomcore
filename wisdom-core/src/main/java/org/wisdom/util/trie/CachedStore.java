package org.wisdom.util.trie;

import org.wisdom.util.ByteArraySet;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Source which internally caches underlying Source key-value pairs
 *
 * Created by Anton Nashatyrev on 21.10.2016.
 */
public class CachedStore<V> implements Store<byte[], V>, Cloneable{
    private Store<byte[], V> delegated;

    private Store<byte[], V> cache = new ByteArrayMapStore<>();

    private Set<byte[]> deleted = new ByteArraySet();

    public CachedStore(Store<byte[], V> delegated) {
        this.delegated = delegated;
    }

    @Override
    public Optional<V> get(byte[] bytes) {
        if(deleted.contains(bytes)) return Optional.empty();
        Optional<V> o = cache.get(bytes);
        if(o.isPresent()) return o;
        return delegated.get(bytes);
    }

    @Override
    public void put(byte[] bytes, V v) {
        deleted.remove(bytes);
        cache.put(bytes, v);
    }

    @Override
    public void putIfAbsent(byte[] bytes, V v) {
        if(containsKey(bytes)) return;
        put(bytes, v);
    }

    @Override
    public void remove(byte[] bytes) {
        cache.remove(bytes);
        deleted.add(bytes);
    }

    @Override
    public boolean flush() {
        if(cache.isEmpty() && deleted.isEmpty()) return false;
        deleted.forEach(delegated::remove);
        cache.keySet().forEach(x -> delegated.put(x, cache.get(x).get()));
        deleted = new ByteArraySet();
        cache = new ByteArrayMapStore<>();
        return delegated.flush();
    }

    @Override
    public Set<byte[]> keySet() {
        Set<byte[]> set  = delegated.keySet();
        set.removeAll(deleted);
        set.addAll(cache.keySet());
        return set;
    }

    @Override
    public Collection<V> values() {
        return keySet().stream().map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public boolean containsKey(byte[] bytes) {
        return !deleted.contains(bytes) && (cache.containsKey(bytes) || delegated.containsKey(bytes));
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        cache = new ByteArrayMapStore<>();
        deleted = delegated.keySet();
    }

    @Override
    public CachedStore<V> clone() {
        CachedStore<V> s = new CachedStore<>(delegated);
        s.deleted = new ByteArraySet(deleted);
        s.cache = new ByteArrayMapStore<>(cache);
        return s;
    }

}
