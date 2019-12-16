package org.wisdom.util.trie;

import lombok.AccessLevel;
import lombok.Getter;
import org.wisdom.util.ByteArraySet;

@Getter(AccessLevel.PACKAGE)
class ScanKeySet implements ScannerAction{
    private ByteArraySet bytes = new ByteArraySet();

    @Override
    public void accept(TrieKey path, Node node) {
        if (node.getType() != Node.Type.EXTENSION && node.getValue() != null) {
            bytes.add(path.toNormal());
        }
    }
}
