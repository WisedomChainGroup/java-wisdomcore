/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.wisdom.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.wisdom.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.util.ByteUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

import static java.util.Arrays.copyOfRange;


public class HashUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HashUtil.class);

    public static final byte[] EMPTY_DATA_HASH;

    private static final String HASH_256_ALGORITHM_NAME = "SHA3-256";
    private static final String HASH_512_ALGORITHM_NAME = "SHA3-512";

    public static final byte[] SIPHASH_KEY = Hex.decode("000102030405060708090a0b0c0d0e0f");

    static {
        Security.addProvider(new BouncyCastleProvider());
        EMPTY_DATA_HASH = sha3(ByteUtil.EMPTY_BYTE_ARRAY);
    }

    private static byte[] hash(byte[] in, String algorithm){
        try{
            MessageDigest digest = MessageDigest.getInstance(algorithm, "BC");
            return digest.digest(in);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param input
     *            - data for hashing
     * @return - sha256 hash of the data
     */
    public static byte[] sha256(byte[] input) {
        return hash(input, "SHA-256");
    }

    public static byte[] keccak256(byte[] in){
        return hash(in, "KECCAK-256");
    }

    public static byte[] whirlPool(byte[] in){
        return hash(in, "WHIRLPOOL");
    }

    public static byte[] ripemd256(byte[] in){
        return hash(in, "RIPEMD256");
    }

    public static byte[] blake2b256(byte[] in){
        return hash(in, "BLAKE2B-256");
    }

    public static byte[] sha3256(byte[] in){
        return hash(in, "SHA3-256");
    }

    public static byte[] skein256256(byte[] in){
        return hash(in, "Skein-256-256");
    }

    public static byte[] sha3(byte[] input) {
        return hash(input, HASH_256_ALGORITHM_NAME);
    }

    public static byte[] sha3(byte[] input1, byte[] input2) {
        return sha3(Arrays.concatenate(input1, input2));
    }

    public static byte[] sha512(byte[] input) {
        return hash(input, HASH_512_ALGORITHM_NAME);
    }

    /**
     * @param data
     *            - message to hash
     * @return - reipmd160 hash of the message
     */
    public static byte[] ripemd160(byte[] data) {
        return hash(data, "RIPEMD160");
    }

    /**
     * Calculates RIGTMOST160(SHA3(input)). This is used in address
     * calculations. *
     *
     * @param input
     *            - data
     * @return - 20 right bytes of the hash keccak of the data
     */
    public static byte[] sha3omit12(byte[] input) {
        byte[] hash = sha3(input);
        return copyOfRange(hash, 12, hash.length);
    }

    /**
     * The way to calculate new address inside ethereum for }
     * sha3(0xff ++ msg.sender ++ salt ++ sha3(init_code)))[12:]
     *
     * @param senderAddr - creating address
     * @param initCode - contract init code
     * @param salt - salt to make different result addresses
     * @return new address
     */
    public static byte[] calcSaltAddr(byte[] senderAddr, byte[] initCode, byte[] salt) {
        // 1 - 0xff length, 32 bytes - keccak-256
        byte[] data = new byte[1 + senderAddr.length + salt.length + 32];
        data[0] = (byte) 0xff;
        int currentOffset = 1;
        System.arraycopy(senderAddr, 0, data, currentOffset, senderAddr.length);
        currentOffset += senderAddr.length;
        System.arraycopy(salt, 0, data, currentOffset, salt.length);
        currentOffset += salt.length;
        byte[] sha3InitCode = sha3(initCode);
        System.arraycopy(sha3InitCode, 0, data, currentOffset, sha3InitCode.length);

        return sha3omit12(data);
    }

    /**
     * @see #doubleDigest(byte[], int, int)
     *
     * @param input
     *            -
     * @return -
     */
    public static byte[] doubleDigest(byte[] input) {
        return doubleDigest(input, 0, input.length);
    }

    /**
     * Calculates the SHA-256 hash of the given byte range, and then hashes the
     * resulting hash again. This is standard procedure in Bitcoin. The
     * resulting hash is in big endian form.
     *
     * @param input
     *            -
     * @param offset
     *            -
     * @param length
     *            -
     * @return -
     */
    public static byte[] doubleDigest(byte[] input, int offset, int length) {
        try {
            MessageDigest sha256digest = MessageDigest.getInstance("SHA-256");
            sha256digest.reset();
            sha256digest.update(input, offset, length);
            byte[] first = sha256digest.digest();
            return sha256digest.digest(first);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Can't find such algorithm", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return - generate random 32 byte hash
     */
    public static byte[] randomHash() {

        byte[] randomHash = new byte[32];
        Random random = new Random();
        random.nextBytes(randomHash);
        return randomHash;
    }

    public static String shortHash(byte[] hash) {
        return Hex.toHexString(hash).substring(0, 6);
    }
}
