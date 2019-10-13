package org.wisdom.tools;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.util.Address;

/**
 * 批量生成账户
 */
public class AccountTool {
    public static void main(String[] args){
        for(int i = 0; i < 10; i++){
            KeyPair keyPair = Ed25519.GenerateKeyPair();
            System.out.println("private key = " + Hex.encodeHexString(keyPair.getPrivateKey().getEncoded()));
            System.out.println("public key = " + Hex.encodeHexString(keyPair.getPublicKey().getEncoded()));
            System.out.println("public key hash = " + Hex.encodeHexString(Address.publicKeyToHash(keyPair.getPublicKey().getEncoded())));
            System.out.println("address = " + Address.publicKeyToAddress(keyPair.getPublicKey().getEncoded()));
            System.out.println("===================================================");
            System.out.println("===================================================");
        }
    }
}
