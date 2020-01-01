package org.wisdom.db;

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

    // commit blocks
    default void commit(List<Block> blocks){
        blocks.forEach(this::commit);
    }
}
