package org.wisdom.util.trie;

public class MemoryCachedStore<V> extends CachedStore<byte[], V> implements Cloneable{
    public MemoryCachedStore(Store<byte[], V> delegated) {
        super(delegated);
    }

    @Override
    Store<byte[], V> newCache() {
        return new ByteArrayMapStore<>();
    }

    @Override
    Store<byte[], V> newDeleted() {
        return new ByteArrayMapStore<>();
    }

    public MemoryCachedStore<V> clone(){
        MemoryCachedStore<V> mem = new MemoryCachedStore<>(delegated);
        mem.cache = new ByteArrayMapStore<>(cache);
        mem.delegated = new ByteArrayMapStore<>(delegated);
        return mem;
    }
}
