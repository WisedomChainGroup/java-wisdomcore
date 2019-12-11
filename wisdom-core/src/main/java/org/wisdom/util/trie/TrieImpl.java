package org.wisdom.util.trie;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.tdf.rlp.RLPItem;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


// enhanced radix tree
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrieImpl<K, V> implements Trie<K, V> {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private Node root;

    HashFunction function;

    Store<byte[], byte[]> cache;

    Codec<K, byte[]> kCodec;

    Codec<V, byte[]> vCodec;

    private TrieImpl() {
    }

    public TrieImpl(HashFunction function,
                    Store<byte[], byte[]> store,
                    Codec<K, byte[]> kCodec,
                    Codec<V, byte[]> vCodec
    ) {
        this.function = function;
        this.cache = store;
        this.kCodec = kCodec;
        this.vCodec = vCodec;
    }

    @Override
    public Optional<V> get(@NonNull K k) {
        byte[] data = kCodec.getEncoder().apply(k);
        if (data == null || data.length == 0) throw new IllegalArgumentException("key cannot be null");
        if (root == null) return Optional.empty();
        return Optional.ofNullable(root.get(TrieKey.fromNormal(data))).map(vCodec.getDecoder());
    }

    public void put(@NonNull K k, @NonNull V val) {
        putBytes(kCodec.getEncoder().apply(k), vCodec.getEncoder().apply(val));
    }

    private void putBytes(byte[] bytes, byte[] bytes2) {
        if (bytes == null || bytes.length == 0) throw new IllegalArgumentException("key cannot be null");
        if (bytes2 == null || bytes2.length == 0) {
            removeBytes(bytes);
            return;
        }
        if (root == null) {
            root = Node.newLeaf(TrieKey.fromNormal(bytes), bytes2);
            return;
        }
        root.insert(TrieKey.fromNormal(bytes), bytes2, cache);
    }

    @Override
    public void putIfAbsent(@NonNull K k, @NonNull V val) {
        if (containsKey(k)) return;
        put(k, val);
    }

    @Override
    public void remove(@NonNull K k) {
        byte[] data = kCodec.getEncoder().apply(k);
        if (data == null || data.length == 0) return;
        if (root == null) return;
        root = root.delete(TrieKey.fromNormal(data), cache);
    }

    private void removeBytes(byte[] data) {
        if (root == null) return;
        if (data == null || data.length == 0) throw new IllegalArgumentException("key cannot be null");
        root = root.delete(TrieKey.fromNormal(data), cache);
    }

    @Override
    public Set<K> keySet() {
        if (root == null) return Collections.emptySet();
        ScanKeySet action = new ScanKeySet();
        root.traverse(TrieKey.EMPTY, action);
        if (kCodec.equals(Codec.identity())) return (Set<K>) action.getBytes();
        return action.getBytes().stream()
                .map(kCodec.getDecoder())
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        if (root == null) return Collections.emptySet();
        ScanValues action = new ScanValues();
        root.traverse(TrieKey.EMPTY, action);
        if (vCodec.equals(Codec.identity())) return (Set<V>) action.getBytes();
        return action.getBytes().stream()
                .map(vCodec.getDecoder())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean containsKey(@NonNull K k) {
        return get(k).isPresent();
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return root != null;
    }

    @Override
    public void clear() {
        root = null;
    }


    public byte[] getRootHash() {
        commit();
        if (root == null) return function.apply(RLPItem.NULL.getEncoded());
        return root
                // commit to cache, the root is still dirty
                .commit(function, new CachedStore<>(cache), true, true)
                .getAsItem().get();
    }

    public byte[] commit() {
        if (root == null) return function.apply(RLPItem.NULL.getEncoded());
        this.root.commit(function, cache, true, false);
        return root.getHash();
    }

    @Override
    public boolean flush() {
        commit();
        return this.cache.flush();
    }

    public boolean isDirty() {
        return root != null && root.isDirty();
    }

    @Override
    public TrieImpl<K, V> moveTo(byte[] rootHash) {
        return new TrieImpl<>(
                Node.fromEncoded(rootHash, new ReadOnlyStore<>(cache)),
                function, cache, kCodec, vCodec
        );
    }
}
