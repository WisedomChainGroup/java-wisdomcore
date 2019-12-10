package org.wisdom.util.trie;

import lombok.Setter;
import org.tdf.rlp.RLPItem;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

// enhanced radix tree
public class TrieImpl implements Trie {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private Node root;

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
    public Optional<byte[]> get(byte[] bytes) {
        if (root == null) return Optional.empty();
        return Optional.ofNullable(root.get(TrieKey.fromNormal(bytes)));
    }

    @Override
    public void put(byte[] bytes, byte[] bytes2) {
        if (bytes == null || bytes.length == 0) throw new IllegalArgumentException("key cannot be null");
        if (bytes2 == null || bytes2.length == 0) {
            remove(bytes);
            return;
        }
        if (root == null) {
            root = Node.newLeaf(TrieKey.fromNormal(bytes), bytes2);
            return;
        }
        root.insert(TrieKey.fromNormal(bytes), bytes2, delete ? cache : null);
    }

    @Override
    public void putIfAbsent(byte[] bytes, byte[] bytes2) {
        if (root != null && root.get(TrieKey.fromNormal(bytes)) != null) return;
        put(bytes, bytes2);
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
    public Collection<byte[]> values() {
        if (root == null) return Collections.emptySet();
        ScanValues action = new ScanValues();
        root.traverse(TrieKey.EMPTY, action);
        return action.getBytes();
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
    public Trie rollback(byte[] rootHash) {
        return new TrieImpl(function, cache, rootHash);
    }

    @Override
    public void flush() {
        if (root == null) return;
        this.root.encodeAndCommit(function, cache, true, true);
    }
}
