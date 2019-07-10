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

package org.wisdom.keystore.crypto;


import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.util.encoders.Hex;


public class SHA3Utility {

    /**
     * Generate hash of the given input using the given Digest.
     *
     * @param input  input data.
     * @param digest the digest to use for hashing
     * @return hashed data.
     */
    public static byte[] hash(byte[] input, Digest digest) {
        byte[] retValue = new byte[digest.getDigestSize()];
        digest.update(input, 0, input.length);
        digest.doFinal(retValue, 0);
        return retValue;
    }
    /**
     *
     * @param hashbytes
     * @return hex format string
     */
    public static String hashToHexString(byte[] hashbytes){
        return Hex.toHexString(hashbytes);
        //return Hex.toHexString(hash(param.toString().getBytes(UTF_8), new SHA3Digest()));
    }

    public static byte[] keccak512(byte[] in){
        Digest digest = new KeccakDigest(512);
        return SHA3Utility.hash(in, digest);
    }

    public static byte[] keccak256(byte[] in){
        Digest digest = new KeccakDigest(256);
        return SHA3Utility.hash(in, digest);
    }

    public static byte[] sha3256(byte[] in){
        Digest digest = new SHA3Digest(256);
        return SHA3Utility.hash(in, digest);
    }


    /*
    public static String shake128(byte[] bytes) {
        Digest digest = new SHAKEDigest(128);
        digest.update(bytes, 0, bytes.length);
        byte[] rsData = new byte[digest.getDigestSize()];
        digest.doFinal(rsData, 0);
        return Hex.toHexString(rsData);
    }

    // SHAKE-256 算法
    public static String shake256(byte[] bytes) {
        Digest digest = new SHAKEDigest(256);
        digest.update(bytes, 0, bytes.length);
        byte[] rsData = new byte[digest.getDigestSize()];
        digest.doFinal(rsData, 0);
        return Hex.toHexString(rsData);
    }
     */

    public static void main(String[] args) {
        System.out.println(org.apache.commons.codec.binary.Hex.encodeHexString(keccak256("abc".getBytes())));
    }
}