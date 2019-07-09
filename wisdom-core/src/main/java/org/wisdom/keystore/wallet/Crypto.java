package org.wisdom.keystore.wallet;

public class Crypto {
    public Crypto() {
    }

    public Crypto(String cipher, String ciphertext, Cipherparams cipherparams) {
        this.cipher = cipher;
        this.ciphertext = ciphertext;
        this.cipherparams = cipherparams;
    }

    public String cipher;
    public String ciphertext;
    public Cipherparams cipherparams;

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public Cipherparams getCipherparams() {
        return cipherparams;
    }

    public void setCipherparams(Cipherparams cipherparams) {
        this.cipherparams = cipherparams;
    }
}

