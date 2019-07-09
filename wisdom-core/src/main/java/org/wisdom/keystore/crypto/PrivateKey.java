package org.wisdom.keystore.crypto;



import org.wisdom.keystore.util.ByteUtils;
import org.wisdom.keystore.util.ByteUtils;

import java.math.BigInteger;
import java.util.Arrays;

public class PrivateKey implements java.security.PrivateKey {
    // private key must less or equals t
    private static final String t = "1000000000000000000000000000000014def9dea2f79cd65812631a5cf5d3ec";

    private byte[] k;

    private String algorithm;
    private String format;

    public String getAlgorithm(){
        return algorithm;
    }

    public String getFormat(){
        return format;
    }

    public PrivateKey(){}

    public PrivateKey(byte[] k, String algorithm, String format) {
        this.k = k;
        this.algorithm = algorithm;
        this.format = format;
    }

    public PrivateKey(byte[] k){
        this.k = k;
    }


    public void setBytes(byte[] k){
    }

    public void setEncoded(byte[] k){
    }

    public void setValid(boolean t){

    }

    public void setDestroyed(boolean t){

    }
    /**
     * check the validity of the private key
     * @return validity
     */
    public boolean isValid(){
        return new BigInteger(this.k).compareTo(new BigInteger(ByteUtils.hexStringToBytes(t))) <= 0;
    }

    public byte[] getBytes(){
        return Arrays.copyOfRange(this.k,0, this.k.length);
    }

    public byte[] getEncoded(){
        return this.getBytes();
    }
}
