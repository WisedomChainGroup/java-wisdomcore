package org.wisdom.util.trie;

import lombok.AccessLevel;
import lombok.Getter;
import org.wisdom.util.ByteArraySet;

import java.util.Set;

@Getter(AccessLevel.PACKAGE)
class Dump implements ScannerAction{
    private Set<byte[]> keys = new ByteArraySet();

    @Override
    public void accept(TrieKey path, Node node) {
        if(node.getHash() != null) keys.add(node.getHash());
    }
}
