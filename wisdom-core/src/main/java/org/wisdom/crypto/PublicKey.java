package org.wisdom.crypto;

public interface PublicKey extends java.security.PublicKey {
    boolean verify(byte[] msg, byte[] signature);
}
