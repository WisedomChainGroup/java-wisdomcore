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

import com.google.common.primitives.Bytes;

import java.math.BigInteger;
import java.util.Arrays;

public class ArrayUtils {
    /**
     * Returns a byte array containing the two's-complement
     * representation of this BigInteger.  The byte array will be in
     * <i>big-endian</i> byte-order: the most significant byte is in
     * the zeroth element.  The array will contain the minimum number
     * of bytes required to represent this BigInteger, including at
     * least one sign bit, which is {@code (ceil((this.bitLength() +
     * 1)/8))}.  (This representation is compatible with the
     * @param r the big integer
     * @param length length of slice
     * @return
     */
    public static byte[] toByteArray(BigInteger r, int length){
        byte[] res = r.toByteArray();
        return Arrays.copyOfRange(res, 0, length - 1);
    }

    /**
     * divide an array into two parts
     * @param in arrays to split
     * @param length the length of first part
     * @return
     */
    public static byte[][] split(byte[] in , int length){
        byte[] p1 = Arrays.copyOfRange(in, 0, length);
        byte[] p2 = Arrays.copyOfRange(in, length, in.length);
        return new byte[][]{p1, p2};
    }

    /**
     * Translates a byte array containing the two's-complement binary
     * representation of a BigInteger into a BigInteger.  The input array is
     * assumed to be in <i>big-endian</i> byte-order: the most significant
     * byte is in the zeroth element.
     *
     * @param  val big-endian two's-complement binary representation of
     *         BigInteger.
     */
    public static BigInteger toBigInteger(byte[] val){
        return new BigInteger(val);
    }

    public static byte[] concat(byte[] p1, byte[] p2){
        return Bytes.concat(p1, p2);
    }

    /**
     * Compares this BigInteger with the specified BigInteger.  This
     * method is provided in preference to individual methods for each
     * of the six boolean comparison operators ({@literal <}, ==,
     * {@literal >}, {@literal >=}, !=, {@literal <=}).  The suggested
     * idiom for performing these comparisons is: {@code
     * (x.compareTo(y)} &lt;<i>op</i>&gt; {@code 0)}, where
     * &lt;<i>op</i>&gt; is one of the six comparison operators.
     *
     * @param p1 one array to be tested for equality
     * @param p2 the other array to be tested for equality
     * @return -1, 0 or 1 as this BigInteger is numerically less than, equal
     *         to, or greater than {@code val}.
     */
    public static int isEqualConstantTime(byte[] p1, byte[] p2){
        BigInteger r1 = new BigInteger(p1);
        BigInteger r2 = new BigInteger(p2);
        return r1.compareTo(r2);
    }
}