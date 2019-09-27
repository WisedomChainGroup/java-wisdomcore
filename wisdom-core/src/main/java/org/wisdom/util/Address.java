package org.wisdom.util;

import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.util.Base58Utility;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.util.Arrays;

public class Address {
    public static byte[] publicKeyToHash(byte[] publicKey) {
        return RipemdUtility.ripemd160(SHA3Utility.keccak256(publicKey));
    }

    public static String publicKeyToAddress(byte[] publicKey) {
        return publicKeyHashToAddress(publicKeyToHash(publicKey));
    }

    public static String publicKeyHashToAddress(byte[] publicKeyHash) {
        byte[] r2 = ByteUtil.prepend(publicKeyHash, (byte) 0);
        byte[] r3 = SHA3Utility.keccak256(SHA3Utility.keccak256(publicKeyHash));
        byte[] b4 = ByteUtil.bytearraycopy(r3, 0, 4);
        byte[] b5 = ByteUtil.byteMerger(r2, b4);
        return Base58Utility.encode(b5);
    }


    public static byte[] addressToPublicKeyHash(String address) {
        if (!verifyAddress(address)){
            return null;
        };
        byte[] r5 = Base58Utility.decode(address);
        byte[] r2 = ByteUtil.bytearraycopy(r5, 0, 21);
        return ByteUtil.bytearraycopy(r2, 1, 20);
    }

    private static boolean verifyAddress(String address) {
        byte[] r5 = Base58Utility.decode(address);
//        ResultSupport ar = new ResultSupport();
        if (!address.startsWith("1")) {//地址不是以"1"开头
            return false;
        }
        byte[] r3 = SHA3Utility.keccak256(SHA3Utility.keccak256(KeystoreAction.addressToPubkeyHash(address)));
        byte[] b4 = ByteUtil.bytearraycopy(r3, 0, 4);
        byte[] _b4 = ByteUtil.bytearraycopy(r5, r5.length - 4, 4);
        //正确
        return Arrays.equals(b4, _b4);
    }
}
