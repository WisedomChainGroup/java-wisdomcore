package org.wisdom.util.trie;


public interface Trie<K, V> extends Store<K, V> {
    // get root hash of current trie, this method will not commit to db
    byte[] getRootHash();
    // commit current trie to db and return the root hash
    byte[] commit();
    // if has modification not sync to db, the trie is dirty
    boolean isDirty();
    // move to another root hash
    Trie<K, V> moveTo(byte[] rootHash);
}
