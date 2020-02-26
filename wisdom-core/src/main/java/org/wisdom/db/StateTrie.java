package org.wisdom.db;

import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.wisdom.core.Block;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StateTrie<T> {
    // get an optional state at a block
    Optional<T> get(byte[] blockHash, byte[] publicKeyHash);
    Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys);
    // commit a new block
    void commit(Block block);

    // get a read only trie for query
    Trie<byte[], T> getTrieByBlockHash(byte[] blockHash);

    // commit blocks
    default void commit(List<Block> blocks){
        blocks.forEach(this::commit);
    }

    // commit pre-generated states
    void commit(Map<byte[], T> states, byte[] blockHash);

    // collect garbage
    void gc(Collection<? extends byte[]> blockHash);

    Store<byte[], byte[]> getTrieStore();
}
