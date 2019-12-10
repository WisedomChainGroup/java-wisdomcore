package org.wisdom.util.trie;

public interface ScannerAction {
    void accept(TrieKey path, Node node);
}
