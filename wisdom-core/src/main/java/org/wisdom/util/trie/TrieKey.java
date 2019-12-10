package org.wisdom.util.trie;

import org.bouncycastle.util.encoders.Hex;

// HEX encoding contains one byte for each nibble of the key and an optional trailing
// 'terminator' byte of value 0x10 which indicates whether or not the node at the key
// contains a value. Hex key encoding is used for nodes loaded in memory because it's
// convenient to access.
//
// COMPACT encoding is defined by the Ethereum Yellow Paper (it's called "hex prefix
// encoding" there) and contains the bytes of the key and a flag. The high nibble of the
// first byte contains the flag; the lowest bit encoding the oddness of the length and
// the second-lowest encoding whether the node at the key is a value node. The low nibble
// of the first byte is zero in the case of an even number of nibbles and the first nibble
// in the case of an odd number. All remaining nibbles (now an even number) fit properly
// into the remaining bytes. Compact encoding is used for nodes stored on disk.
public class TrieKey {
    public static final int ODD_OFFSET_FLAG = 0x1;
    public static final int TERMINATOR_FLAG = 0x2;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private final byte[] data;
    private final int offset;

    static TrieKey EMPTY = new TrieKey(EMPTY_BYTE_ARRAY, 0);

    public static TrieKey fromNormal(byte[] key) {
        return new TrieKey(key);
    }

    public static TrieKey fromPacked(byte[] data) {
        // flag = data[0] >> 4
        // flag & ODD_OFFSET_FLAG != 0 -> the length is odd, we drop the first hex
        // flag & ODD_OFFSET_FLAG == 0 -> the length is event, the first hex is flag and second hex is useless
        return new TrieKey(data, ((data[0] >> 4) & ODD_OFFSET_FLAG) != 0 ? 1 : 2);
    }

    static boolean isTerminal(byte[] packed){
        return ((packed[0] >> 4) & TERMINATOR_FLAG) != 0;
    }


    public static TrieKey single(int hex) {
        TrieKey ret = new TrieKey(new byte[1], 1);
        ret.set(0, hex);
        return ret;
    }

    private TrieKey(byte[] data) {
        this(data, 0);
    }

    private TrieKey(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

    public byte[] toPacked(boolean terminal) {
        // offset & 1 == 0 -> length is even
        // offset & 1 != 0 -> length is odd
        int flags = ((offset & 1) != 0 ? ODD_OFFSET_FLAG : 0) | (terminal ? TERMINATOR_FLAG : 0);
        // prepend an empty byte to store flag and
        // if size is odd, size()/2 + 1 == bytes length
        // if size is even , size()/2 + 1 == bytes length + 1
        byte[] ret = new byte[size() / 2 + 1];
        // if size is odd, copy all with first byte, and store flag to this byte, since the first half of this byte is useless
        // if size is even, copy all without first byte, store flag to first half of first byte
        int toCopy = (flags & ODD_OFFSET_FLAG) != 0 ? ret.length : ret.length - 1;
        // copy from tail to tail, since data before offset is ignored
        System.arraycopy(data, data.length - toCopy, ret, ret.length - toCopy, toCopy);
        // set first half of first byte to zero
        ret[0] &= 0x0F;
        // store flag to first half of first byte
        ret[0] |= flags << 4;
        return ret;
    }

    public byte[] toNormal() {
        if ((offset & 1) != 0) throw new RuntimeException("Can't convert a key with odd number of hexes to normal: " + this);
        int arrLen = data.length - offset / 2;
        byte[] ret = new byte[arrLen];
        System.arraycopy(data, data.length - arrLen, ret, 0, arrLen);
        return ret;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int get(int index) {
        byte b = data[(offset + index) >> 1];
        return (((offset + index) & 1) == 0 ? (b >> 4) : b) & 0xF;
    }

    private void set(int index, int hex) {
        int byteIndex = (offset + index) >> 1;
        if (((offset + index) & 1) == 0) {
            data[byteIndex] &= 0x0F;
            data[byteIndex] |= hex << 4;
        } else {
            data[byteIndex] &= 0xF0;
            data[byteIndex] |= hex;
        }
    }

    public int size() {
        return (data.length << 1) - offset;
    }

    public TrieKey concat(TrieKey that) {
        int size = size();
        int thatSize = that.size();
        int newSize = size + thatSize;
        byte[] newBytes = new byte[(newSize + 1) >> 1];
        TrieKey ret = new TrieKey(newBytes, newSize & 1);
        for (int i = 0; i < size; i++) {
            ret.set(i, get(i));
        }
        for (int i = 0; i < thatSize; i++) {
            ret.set(size + i, that.get(i));
        }
        return ret;
    }

    public TrieKey shift(){
        return shift(1);
    }

    public TrieKey shift(int hexCnt) {
        return new TrieKey(this.data, offset + hexCnt);
    }

    public TrieKey getCommonPrefix(TrieKey k) {
        int prefixLen = 0;
        int thisSize = size();
        int thatSize = k.size();
        while (prefixLen < thisSize && prefixLen < thatSize && get(prefixLen) == k.get(prefixLen))
            prefixLen++;
        byte[] prefixKey = new byte[(prefixLen + 1) >> 1];
        TrieKey ret = new TrieKey(prefixKey, (prefixLen & 1) == 0 ? 0 : 1);
        for (int i = 0; i < prefixLen; i++) {
            ret.set(i, k.get(i));
        }
        return ret;
    }

    public TrieKey matchAndShift(TrieKey that) {
        int size = size();
        int thatSize = that.size();
        if (size < thatSize) return null;

        if ((offset & 1) == (that.offset & 1)) {
            // optimization to compare whole keys bytes
            if ((offset & 1) == 1) {
                if (get(0) != that.get(0)) return null;
            }
            int idx1 = (offset + 1) >> 1;
            int idx2 = (that.offset + 1) >> 1;
            int l = thatSize >> 1;
            for (int i = 0; i < l; i++, idx1++, idx2++) {
                if (data[idx1] != that.data[idx2]) return null;
            }
        } else {
            for (int i = 0; i < thatSize; i++) {
                if (get(i) != that.get(i)) return null;
            }
        }
        return shift(thatSize);
    }

    @Override
    public boolean equals(Object obj) {
        TrieKey k = (TrieKey) obj;
        int len = size();

        if (len != k.size()) return false;
        for (int i = 0; i < len; i++) {
            if (get(i) != k.get(i)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Hex.toHexString(data).substring(offset);
    }
}
