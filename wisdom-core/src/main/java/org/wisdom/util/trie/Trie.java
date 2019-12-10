package org.wisdom.util.trie;

public interface Trie<V> extends Store<byte[], V> {
    // commit and generate a snap shot
    byte[] getRootHash();
    Trie<V> rollback(byte[] rootHash);
    void flush();
}
