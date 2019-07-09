package org.wisdom.core.validate;

import org.wisdom.core.Block;

public interface BlockRule {
    Result validateBlock(Block block);
}
