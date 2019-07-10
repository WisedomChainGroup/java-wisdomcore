/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.keystore.util;

import java.nio.ByteBuffer;

public class ByteUtils {
    /**
     * Converts an array of 8 bytes into a long.
     *
     * @param bytes The bytes.
     * @return The long.
     */
    public static long bytesToLong(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, 8);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * Converts a long value into an array of 8 bytes.
     *
     * @param x The long.
     * @return The bytes.
     */
    public static byte[] longToBytes(final long x) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Converts an array of 4 bytes into a int.
     *
     * @param bytes The bytes.
     * @return The int.
     */
    public static int bytesToInt(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(bytes, 0, 4);
        buffer.flip();
        return buffer.getInt();
    }

    /**
     * Converts an int value into an array of 4 bytes.
     *
     * @param x The int.
     * @return The bytes.
     */
    public static byte[] intToBytes(final int x) {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(x);
        return buffer.array();
    }

    /**
     * Constant-time byte comparison. The constant time behavior eliminates side channel attacks.
     *
     * @param b One byte.
     * @param c Another byte.
     * @return 1 if b and c are equal, 0 otherwise.
     */
    public static int isEqualConstantTime(final int b, final int c) {
        int result = 0;
        final int xor = b ^ c;
        for (int i = 0; i < 8; i++) {
            result |= xor >> i;
        }

        return (result ^ 0x01) & 0x01;
    }


    /**
     * byte array convert into hex string
     *
     * @param bytes
     * @return convert to hex string and keep the bytes width
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return "";
        if (bytes.length == 0)
            return "";

        StringBuffer result = new StringBuffer();
        String hex;

        for (int i = 0; i < bytes.length; i++) {
            hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }


    /**
     * hex string convert into byte array
     *
     * @param hexString not include 0x prefix
     * @return byte array
     */
    public static byte[] hexStringToBytes(String hexString) {

        if (StringUtil.isNullOrEmpty(hexString)) {
            return new byte[0];
        }

        int length = hexString.length() / 2;
        byte[] byteArray = new byte[length];
        for (int i = 0; i < length; i++) {
            byteArray[i] = (byte) Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return byteArray;
    }




    /**
     * Constant-time check if byte is negative. The constant time behavior eliminates side channel attacks.
     *
     * @param b The byte to check.
     * @return 1 if the byte is negative, 0 otherwise.
     */
    public static int isNegativeConstantTime(final int b) {
        return (b >> 8) & 1;
    }

    /**
     * Creates a human readable representation of an array of bytes.
     *
     * @param bytes The bytes.
     * @return An string representation of the bytes.
     */
    public static String toString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        for (final byte b : bytes) {
            builder.append(String.format("%02X ", (byte) (0xFF & b)));
        }

        builder.append("}");
        return builder.toString();
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

}