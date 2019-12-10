package org.wisdom.util.trie;

import java.util.function.Function;

// the hash function should return a 32 bytes length byte array
@FunctionalInterface
public interface HashFunction extends Function<byte[], byte[]> {
}
