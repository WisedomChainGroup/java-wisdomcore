package org.wisdom.db;

import org.wisdom.contract.Asset;
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
    private Asset Asset;//合约代币
    private int type;//1是普通地址，0是合约地址
    private Map<String, byte[]> ContractMap;//合约数据

    public AccountState() {
        this.account = new Account();
        this.interestMap = new HashMap<>();
        this.ShareMap = new HashMap<>();
        this.type = 1;
        this.ContractMap = new HashMap<>();
    }

    public AccountState(Asset Asset) {
        this.type = 0;
        this.Asset = Asset;
        this.account = new Account();
        account.setPubkeyHash(new byte[20]);//合约hash
    }

    public AccountState(byte[] pubkeyHash) {
        this();
        account.setPubkeyHash(pubkeyHash);
    }

    public AccountState(Account account, Map<String, Incubator> interestMap, Map<String, Incubator> shareMap, Map<String, byte[]> contractMap) {
        this.account = account;
        this.interestMap = interestMap;
        this.ShareMap = shareMap;
        this.ContractMap = contractMap;
        this.type = 1;

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

    public org.wisdom.contract.Asset getAsset() {
        return Asset;
    }

    public void setAsset(org.wisdom.contract.Asset asset) {
        Asset = asset;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, byte[]> getContractMap() {
        return ContractMap;
    }

    public void setContractMap(Map<String, byte[]> contractMap) {
        ContractMap = contractMap;
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
        if (type == 1) {
            accountState.setAccount(account.copy());
            accountState.setInterestMap(new HashMap<>());
            for (String k : interestMap.keySet()) {
                accountState.getInterestMap().put(k, interestMap.get(k).copy());
            }
            accountState.setShareMap(new HashMap<>());
            for (String k : getShareMap().keySet()) {
                accountState.getShareMap().put(k, ShareMap.get(k).copy());
            }
            accountState.setContractMap(new HashMap<>());
            for (String k : getContractMap().keySet()) {
                accountState.getContractMap().put(k, ContractMap.get(k));
            }
        } else {
            accountState.setAsset(Asset.copy());
        }
        accountState.setType(type);
        return accountState;
    }
}
