package org.wisdom.util.trie;

import java.util.function.BiConsumer;

public interface ScannerAction extends BiConsumer<TrieKey, Node> {
}
