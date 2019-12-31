package org.wisdom.db;

import org.tdf.common.util.ChainedWrapper;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.Block;

public class BlockWrapper extends ChainedWrapper<Block> {
    public BlockWrapper(Block block) {
        super(HexBytes.fromBytes(block.hashPrevBlock), HexBytes.fromBytes(block.getHash()), block);
    }
}
