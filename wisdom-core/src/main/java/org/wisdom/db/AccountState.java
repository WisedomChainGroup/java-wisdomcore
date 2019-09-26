package org.wisdom.db;

import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 包含了账户的所有信息，包括余额，孵化的金额等，提供深拷贝方法
 */
public class AccountState {
    private Account account;
    private Map<String, Incubator> interestMap;
    private Map<String, Incubator> ShareMap;

    public AccountState() {
        account = new Account();
        interestMap = new HashMap<>();
        ShareMap = new HashMap<>();
    }

    public AccountState(byte[] pubkeyHash) {
        this();
        account.setPubkeyHash(pubkeyHash);
    }


    public AccountState(Account account, Map<String, Incubator> interestMap, Map<String, Incubator> shareMap) {
        this.account = account;
        this.interestMap = interestMap;
        ShareMap = shareMap;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Map<String, Incubator> getInterestMap() {
        return interestMap;
    }

    public void setInterestMap(Map<String, Incubator> interestMap) {
        this.interestMap = interestMap;
    }

    public Map<String, Incubator> getShareMap() {
        return ShareMap;
    }

    public void setShareMap(Map<String, Incubator> shareMap) {
        ShareMap = shareMap;
    }

    // 账户地址
    public String takeAddress() {
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

    public AccountState copy() {
        AccountState accountState = new AccountState();
        accountState.setAccount(account.copy());
        accountState.setInterestMap(new HashMap<>());
        for (String k : interestMap.keySet()) {
            accountState.getInterestMap().put(k, interestMap.get(k).copy());
        }
        accountState.setShareMap(new HashMap<>());
        for (String k : getShareMap().keySet()) {
            accountState.getShareMap().put(k, ShareMap.get(k).copy());
        }
        return accountState;
    }
}
