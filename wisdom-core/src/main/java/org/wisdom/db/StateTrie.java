package org.wisdom.db;

import org.wisdom.core.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface StateTrie<T> {
    // get an optional state at a block
    Optional<T> get(byte[] blockHash, byte[] publicKeyHash);
    Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys);
    // commit a new block, if the new block had committed throw exception
    byte[] commit(Block block);
}
