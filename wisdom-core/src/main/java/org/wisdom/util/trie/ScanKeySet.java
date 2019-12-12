package org.wisdom.util.trie;

import lombok.AccessLevel;
import lombok.Getter;
import org.wisdom.util.ByteArraySet;

import java.util.function.BiConsumer;

@Getter(AccessLevel.PACKAGE)
class ScanKeySet implements BiConsumer<TrieKey, Node> {
    private ByteArraySet bytes = new ByteArraySet();

    @Override
    public void accept(TrieKey path, Node node) {
        if (node.getType() != Node.Type.EXTENSION && node.getValue() != null) {
            bytes.add(path.toNormal());
        }
    }
}
