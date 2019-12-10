package org.wisdom.util.trie;

import lombok.Setter;
import org.tdf.rlp.RLPItem;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// enhanced radix tree
public class TrieImpl<V> implements Trie<V> {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private Node root;

    private Function<V, byte[]> serializer;
    private Function<byte[], V> deserializer;

    // this flag determine whether deprecated value will be deleted
    @Setter
    private boolean delete;

    HashFunction function;

    Store<byte[], byte[]> cache;

    public TrieImpl(HashFunction function, Store<byte[], byte[]> cache) {
        this.function = function;
        this.cache = cache;
    }

    public TrieImpl(HashFunction function, Store<byte[], byte[]> cache, byte[] rootHash) {
        this.function = function;
        this.cache = cache;
        this.root = Node.fromEncoded(RLPItem.fromBytes(rootHash), cache);
    }

    @Override
    public Optional<V> get(byte[] bytes) {
        if (root == null) return Optional.empty();
        return Optional.ofNullable(root.get(TrieKey.fromNormal(bytes))).map(deserializer);
    }

    @Override
    public void put(byte[] bytes, V val) {
        if (bytes == null || bytes.length == 0) throw new IllegalArgumentException("key cannot be null");
        byte[] data = val == null ? null : serializer.apply(val);
        if (data == null || data.length == 0) {
            remove(bytes);
            return;
        }
        if (root == null) {
            root = Node.newLeaf(TrieKey.fromNormal(bytes), data);
            return;
        }
        root.insert(TrieKey.fromNormal(bytes), data, delete ? cache : null);
    }

    @Override
    public void putIfAbsent(byte[] bytes, V val) {
        if (root != null && root.get(TrieKey.fromNormal(bytes)) != null) return;
        put(bytes, val);
    }

    @Override
    public void remove(byte[] bytes) {
        if (root == null) return;
        root = root.delete(TrieKey.fromNormal(bytes), delete ? cache : null);
    }

    @Override
    public Set<byte[]> keySet() {
        if (root == null) return Collections.emptySet();
        ScanKeySet action = new ScanKeySet();
        root.traverse(TrieKey.EMPTY, action);
        return action.getBytes();
    }

    @Override
    public Collection<V> values() {
        if (root == null) return Collections.emptySet();
        ScanValues action = new ScanValues();
        root.traverse(TrieKey.EMPTY, action);
        return action.getBytes().stream().map(deserializer).collect(Collectors.toList());
    }

    @Override
    public boolean containsKey(byte[] bytes) {
        if (root == null) return false;
        return root.get(TrieKey.fromNormal(bytes)) != null;
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

    // generate a snap short to recover from
    public byte[] getRootHash() {
        if (root == null) return function.apply(RLPItem.NULL.getEncoded());
        return root.encodeAndCommit(function, cache, true, delete).getAsItem().get();
    }

    @Override
    public Trie<V> rollback(byte[] rootHash) {
        return new TrieImpl<>(function, cache, rootHash);
    }

    @Override
    public void flush() {
        if (root == null) return;
        this.root.encodeAndCommit(function, cache, true, true);
    }
}
