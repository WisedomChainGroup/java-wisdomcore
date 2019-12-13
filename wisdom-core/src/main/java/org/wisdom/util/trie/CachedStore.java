package org.wisdom.util.trie;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Source which internally caches underlying Source key-value pairs
 *
 * Created by Anton Nashatyrev on 21.10.2016.
 */
public abstract class CachedStore<K, V> implements Store<K, V>{
    protected Store<K, V> delegated;

    protected Store<K, V> cache;

    protected Store<K, V> deleted;

    public CachedStore(Store<K, V> delegated) {
        this.delegated = delegated;
        clearCache();
    }

    abstract Store<K, V> newCache();

    abstract Store<K, V> newDeleted();

    void clearCache(){
        cache = newCache();
        deleted = newDeleted();
    }

    @Override
    public Optional<V> get(@NonNull K k) {
        if(deleted.containsKey(k)) return Optional.empty();
        Optional<V> o = cache.get(k);
        if(o.isPresent()) return o;
        return delegated.get(k);
    }

    @Override
    public void put(@NonNull K k, @NonNull V v) {
        deleted.remove(k);
        cache.put(k, v);
    }

    @Override
    public void putIfAbsent(K k, V v) {
        if(containsKey(k)) return;
        put(k, v);
    }

    @Override
    public void remove(@NonNull K k) {
        cache.remove(k);
        Optional<V> v = delegated.get(k);
        if(!v.isPresent()) return;
        deleted.put(k, v.get());
    }

    /**
     * flush cache to underlying database
     */
    @Override
    public void flush() {
        if(cache.isEmpty() && deleted.isEmpty()) return ;
        deleted.keySet().forEach(delegated::remove);
        cache.keySet().forEach(x -> delegated.put(x, cache.get(x).get()));
        clearCache();
        delegated.flush();
    }

    @Override
    public Set<K> keySet() {
        Set<K> set  = delegated.keySet();
        set.removeAll(deleted.keySet());
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
    public boolean containsKey(K k) {
        return !deleted.containsKey(k) && (cache.containsKey(k) || delegated.containsKey(k));
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
        clearCache();
        deleted = delegated;
    }

}
