package org.wisdom.db;

import org.tdf.common.util.ChainedWrapper;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.Block;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.Arrays;

import java.util.Comparator;

public class BlockWrapper extends ChainedWrapper<Block> {
    private final int hashCode;

    public static final Comparator<BlockWrapper> COMPARATOR = (x, y) -> compareBlock(x.get(), y.get());

    public static int compareBlock(Block a, Block b) {
        if (a.nHeight != b.nHeight) {
            return Long.compare(a.nHeight, b.nHeight);
        }

        if (a.body.get(0).amount != b.body.get(0).amount) {
            return (int) (a.body.get(0).amount - b.body.get(0).amount);
        }
        // pow 更小的占优势
        return -BigEndian.decodeUint256(Block.calculatePOWHash(a))
                .compareTo(
                        BigEndian.decodeUint256(Block.calculatePOWHash(b))
                );
    }

    public BlockWrapper(Block block) {
        super(HexBytes.fromBytes(block.hashPrevBlock), HexBytes.fromBytes(block.getHash()), block);
        this.hashCode = Arrays.hashCode(block.getHash());
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
