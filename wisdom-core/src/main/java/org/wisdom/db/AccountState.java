package org.wisdom.db;

import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;

import java.util.Arrays;
import java.util.Map;

/**
 * 包含了账户的所有信息，包括余额，孵化的金额等，提供深拷贝方法
 */
public class AccountState {
    private Account account;
    private Map<String,Incubator> incubatorMap;
    private Map<String,Incubator> shareincubMap;


    public AccountState(Account account, Map<String, Incubator> incubatorMap, Map<String, Incubator> shareincubMap) {
        this.account = account;
        this.incubatorMap = incubatorMap;
        this.shareincubMap = shareincubMap;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Map<String, Incubator> getIncubatorMap() {
        return incubatorMap;
    }

    public void setIncubatorMap(Map<String, Incubator> incubatorMap) {
        this.incubatorMap = incubatorMap;
    }

    public Map<String, Incubator> getShareincubMap() {
        return shareincubMap;
    }

    public void setShareincubMap(Map<String, Incubator> shareincubMap) {
        this.shareincubMap = shareincubMap;
    }

    // 深拷贝
    public AccountState copy(){
        return null;
    }

    // 账户地址
    public String takeAddress(){
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountState account = (AccountState) o;
        return Arrays.equals(account.getAccount().getPubkeyHash(), account.getAccount().getPubkeyHash());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(account.getPubkeyHash());
    }


}
