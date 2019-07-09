package org.wisdom.encoding;

import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;

public class BigEndian {
    public static final long MAX_UINT_32 = 0x00000000ffffffffL;
    public static final int MAX_UINT_16 = 0x0000ffff;
    public static final BigInteger MAX_UINT_256 = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    private static final BigInteger shadow;

    static {
        byte[] shadowBits = new byte[32];
        shadowBits[0] = (byte) 0xff;
        shadow = new BigInteger(1, shadowBits);
    }


    public static long decodeUint32(byte[] data) {
        return new BigInteger(1, data).longValue();
    }

    // big-endian encoding
    public static byte[] encodeUint32(long value) {
        byte[] res = new byte[4];
        res[0] = (byte) ((value & 0x00000000FF000000L) >>> 24);
        res[1] = (byte) ((value & 0x0000000000FF0000L) >>> 16);
        res[2] = (byte) ((value & 0x000000000000FF00L) >>> 8);
        res[3] = (byte) (value & 0x00000000000000FFL);
        return res;
    }

    // big-endian encoding
    public static byte[] encodeUint64(long value) {
        byte[] res = new byte[8];
        res[0] = (byte) ((value & 0xFF00000000000000L) >>> 56);
        res[1] = (byte) ((value & 0x00FF000000000000L) >>> 48);
        res[2] = (byte) ((value & 0x0000FF0000000000L) >>> 40);
        res[3] = (byte) ((value & 0x000000FF00000000L) >>> 32);
        res[4] = (byte) ((value & 0x00000000FF000000L) >>> 24);
        res[5] = (byte) ((value & 0x0000000000FF0000L) >>> 16);
        res[6] = (byte) ((value & 0x000000000000FF00L) >>> 8);
        res[7] = (byte) (value & 0x00000000000000FFL);
        return res;
    }

    public static long decodeUint64(byte[] data) {
        return new BigInteger(data).longValue();
    }

    public static int compareUint256(byte[] a, byte[] b) {
        return new BigInteger(1, a).compareTo(
                new BigInteger(1, b)
        );
    }

    public static long getMaxUint32() {
        return MAX_UINT_32;
    }

    public static int decodeUint16(byte[] in) {
        return new BigInteger(1, in).intValue();
    }

    public static byte[] encodeUint16(int value) {
        byte[] res = new byte[2];
        res[0] = (byte) ((value & 0x0000ff00) >>> 8);
        res[1] = (byte) (value & 0x000000ff);
        return res;
    }

    public static byte[] encodeUint256(BigInteger in) {
        if (in.signum() < 0) {
            return null;
        }
        if (in.signum() == 0) {
            return new byte[32];
        }
        byte[] res = new byte[32];
        for (int i = 0; i < res.length; i++) {
            BigInteger tmp = in.and(shadow.shiftRight(i * 8)).shiftRight((res.length - i - 1) * 8);
            res[i] = tmp.byteValue();
        }
        return res;
    }

    public static BigInteger decodeUint256(byte[] in) {
        return new BigInteger(1, in);
    }

    public static void main(String[] args) throws Exception{
        System.out.println(Hex.encodeHex(encodeUint16(MAX_UINT_16)));
        System.out.println(Hex.encodeHexString(BigEndian.encodeUint256(BigEndian.decodeUint256(
                Hex.decodeHex("0000afffffffffffffffffffffffffffffffffffffffffffffffffffffffffff".toCharArray())
        ))));
    }
}
