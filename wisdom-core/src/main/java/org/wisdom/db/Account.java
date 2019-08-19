package org.wisdom.db;

import java.util.Arrays;

/**
 * 包含了账户的所有信息，包括余额，孵化的金额等，提供深拷贝方法
 */
public class Account {
    private byte[] publicKeyHash;

    // 深拷贝
    public Account copy(){
        return null;
    }

    // 账户地址
    public String getAddress(){
        return null;
    }

    public byte[] getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(byte[] publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Arrays.equals(publicKeyHash, account.publicKeyHash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(publicKeyHash);
    }
}
