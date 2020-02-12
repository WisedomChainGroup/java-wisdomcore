package org.wisdom.util;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.util.Base58Utility;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.util.Arrays;

public class Address {
    public static byte[] publicKeyToHash(byte[] publicKey) {
        if(publicKey.length != 32) throw new RuntimeException("publicKey must be 32-byte");
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
        return KeystoreAction.addressToPubkeyHash(address);
    }

    private static boolean verifyAddress(String address) {
       if (KeystoreAction.verifyAddress(address) == 0){
           return true;
       }
        return false;
    }

    // 从用户输入的地址、公钥或者公钥哈希转化成公钥哈希
    public static byte[] getPublicKeyHash(String input){
        byte[] publicKeyHash;
        try {
            publicKeyHash = Hex.decodeHex(input);
            if (publicKeyHash.length == Transaction.PUBLIC_KEY_SIZE) {
                publicKeyHash = Address.publicKeyToHash(publicKeyHash);
            }
        } catch (Exception e) {
            publicKeyHash = Address.addressToPublicKeyHash(input);
        }
        return publicKeyHash;
    }
}
