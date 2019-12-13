package org.wisdom.db;

import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;
import org.tdf.rlp.RLPEncoding;
import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.util.ByteArrayMap;
import org.wisdom.util.MapRLPUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 包含了账户的所有信息，包括余额，孵化的金额等，提供深拷贝方法
 */
public class AccountState {
    @RLP(0)
    private Account account;
    @RLP(1)
    @RLPEncoding(MapRLPUtil.IncubMapEncoderDecoder.class)
    @RLPDecoding(MapRLPUtil.IncubMapEncoderDecoder.class)
    private Map<String, Incubator> interestMap;
    @RLP(2)
    @RLPEncoding(MapRLPUtil.IncubMapEncoderDecoder.class)
    @RLPDecoding(MapRLPUtil.IncubMapEncoderDecoder.class)
    private Map<String, Incubator> ShareMap;
    @RLP(3)
    private int type;//0是普通地址,1是合约代币，2是多重签名
    @RLP(4)
    private byte[] Contract;//合约RLP
    @RLP(5)
    @RLPEncoding(MapRLPUtil.TokenMapEncoderDecoder.class)
    @RLPDecoding(MapRLPUtil.TokenMapEncoderDecoder.class)
    private ByteArrayMap<Long> TokensMap;

    public AccountState() {
    }

    public AccountState(byte[] pubkeyHash) {
        this();
        account.setPubkeyHash(pubkeyHash);
    }

    public AccountState(Account account, Map<String, Incubator> interestMap, Map<String, Incubator> shareMap, int type, byte[] Contract, ByteArrayMap<Long> TokensMap) {
        this.account = account;
        this.interestMap = interestMap;
        this.ShareMap = shareMap;
        this.type = type;
        this.Contract = Contract;
        this.TokensMap = TokensMap;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getContract() {
        return Contract;
    }

    public void setContract(byte[] contract) {
        Contract = contract;
    }

    public Map<byte[], Long> getTokensMap() {
        return TokensMap;
    }

    public void setTokensMap(ByteArrayMap<Long> tokensMap) {
        TokensMap = tokensMap;
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
        accountState.setType(type);
        accountState.setContract(Contract);
        accountState.setTokensMap(new ByteArrayMap<>(TokensMap));
        return accountState;
    }
}
