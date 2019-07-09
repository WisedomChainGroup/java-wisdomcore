package org.wisdom.keystore.crypto;

public class Address {

    private final PublicKey publicKey;

    public Address(PublicKey pubkey) {
        this.publicKey = pubkey;
    }

    public String ConvertPublicKeyToAddress(PublicKey pubkey) {

        if (null == pubkey) {
            throw new IllegalArgumentException("public key cannot be null");
        }

        return "";
    }

}
