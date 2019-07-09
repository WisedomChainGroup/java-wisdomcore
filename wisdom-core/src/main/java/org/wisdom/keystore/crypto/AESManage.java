package org.wisdom.keystore.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

// AES-256-CTR encrypt/decrypt
public class AESManage {
    private byte[] iv;

    public static final String cipher = "aes-256-ctr";
    public AESManage(){
    }

    public AESManage(byte[] iv){
        this.iv = iv;
    }

    public byte[] encrypt(byte[] key,byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ivSpec = new IvParameterSpec(this.iv);
        cipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] key,byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ivSpec = new IvParameterSpec(this.iv);
        cipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);
        return cipher.doFinal(data);
    }
}
