package org.wisdom.util.trie;

public interface Trie extends Store<byte[], byte[]> {
    // generate a snap shot
    byte[] getRootHash();
    Trie rollback(byte[] rootHash);
    void flush();
}
