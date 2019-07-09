package org.wisdom.crypto.ed25519;

import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.KeyPair;

/**
 * Ed25519 keypair for signature and verifying
 */
public class Ed25519KeyPair implements KeyPair {
    private Ed25519PrivateKey privateKey;
    private Ed25519PublicKey publicKey;

    public Ed25519PrivateKey getPrivateKey() {
        return privateKey;
    }

    void setPrivateKey(Ed25519PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Ed25519PublicKey getPublicKey() {
        return publicKey;
    }

    void setPublicKey(Ed25519PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
