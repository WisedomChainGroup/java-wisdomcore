package org.wisdom.keystore.crypto;

import java.util.Arrays;

public class PublicKey implements java.security.PublicKey {
    private byte[] k;
    private String algorithm;
    private String format;

    public PublicKey(){}

    public PublicKey(byte[] k){
        this.k = k;
    }

    public PublicKey(byte[] k, String algorithm, String format) {
        this.k = k;
        this.algorithm = algorithm;
        this.format = format;
    }

    public void setBytes(byte[] k) {
    }

    public void setEncoded(byte[] k) {
    }

    public void setFormat(String s){

    }

    public byte[] getBytes(){
        return Arrays.copyOfRange(this.k,0, this.k.length);
    }

    public byte[] getEncoded(){
        return this.getBytes();
    }

    public String getAlgorithm(){
        return algorithm;
    }

    public String getFormat(){
        return format;
    }
}
