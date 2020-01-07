package org.wisdom.db;

import org.tdf.common.util.ChainedWrapper;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.Block;
import org.wisdom.util.Arrays;

public class BlockWrapper extends ChainedWrapper<Block> {
    private final int hashCode;

    public BlockWrapper(Block block) {
        super(HexBytes.fromBytes(block.hashPrevBlock), HexBytes.fromBytes(block.getHash()), block);
        this.hashCode = Arrays.hashCode(block.getHash());
    }

    public BlockWrapper(byte[] hash) {
        this.hashCode = Arrays.hashCode(hash);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockWrapper)) return false;
        return FastByteComparisons.equal(get().getHash(), ((BlockWrapper) obj).get().getHash());
    }
}
