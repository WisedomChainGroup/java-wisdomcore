package org.wisdom.crypto;

public class CryptoException extends RuntimeException {
    public CryptoException(){

    }

    public CryptoException(String msg){
        super(msg);
    }
}
