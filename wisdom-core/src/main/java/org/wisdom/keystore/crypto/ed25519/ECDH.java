package org.wisdom.keystore.crypto.ed25519;




import org.wisdom.keystore.crypto.CryptoException;

import java.security.PublicKey;

public interface ECDH {
    byte[] generateSecretKey(PublicKey publicKey) throws CryptoException;
}
