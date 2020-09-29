package org.wisdom.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Hex;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.ByteArrayMapStore;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPDecoding;
import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.crypto.HashUtil;
import org.wisdom.vm.abi.SafeMath;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * 包含了账户的所有信息，包括余额，孵化的金额等，提供深拷贝方法
 */
public class AccountState {
    @RLP(0)
    private Account account;
    @RLP(1)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Incubator> interestMap;
    @RLP(2)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Incubator> ShareMap;
    @RLP(3)
    private int type;//0是普通地址,1是合约代币，2是多重签名,3是锁定时间哈希,4是锁定高度哈希,5是定额条件比例支付,6是 wasm 合约
    @RLP(4)
    @JsonIgnore
    private byte[] Contract;//合约RLP //
    @RLP(5)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Long> TokensMap;

    public AccountState() {
    }

    public AccountState(byte[] pubkeyHash) {
        this.account = new Account(0, pubkeyHash, 0, 0, 0, 0, 0);
        this.interestMap = new ByteArrayMap<>();
        this.ShareMap = new ByteArrayMap<>();
        this.type = 0;//默认普通地址
        this.Contract = new byte[0];
        this.TokensMap = new ByteArrayMap<>();
    }

    public AccountState(Account account, Map<byte[], Incubator> interestMap, Map<byte[], Incubator> shareMap, int type, byte[] Contract, Map<byte[], Long> TokensMap) {
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

    public Map<byte[], Incubator> getInterestMap() {
        return interestMap;
    }

    public void setInterestMap(Map<byte[], Incubator> interestMap) {
        this.interestMap = interestMap;
    }

    public Map<byte[], Incubator> getShareMap() {
        return ShareMap;
    }

    public void setShareMap(Map<byte[], Incubator> shareMap) {
        ShareMap = shareMap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JsonIgnore
    public byte[] getContract() {
        return Contract;
    }

    public void setContract(byte[] contract) {
        Contract = contract;
    }

    public Map<byte[], Long> getTokensMap() {
        return TokensMap;
    }

    public void setTokensMap(Map<byte[], Long> tokensMap) {
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

    // TODO: is deep copy?
    public AccountState copy() {
        AccountState accountState = new AccountState();
        accountState.setAccount(account.copy());
        accountState.setInterestMap(new ByteArrayMap<>(interestMap));
        accountState.setShareMap(new ByteArrayMap<>(ShareMap));
        accountState.setType(type);
        accountState.setContract(Contract);
        accountState.setTokensMap(new ByteArrayMap<>(TokensMap));
        return accountState;
    }

    @JsonIgnore
    public WASMContract getWASMContract() {
        if (this.Contract.length == 0) {
            return new WASMContract();
        }
        return RLPCodec.decode(this.Contract, WASMContract.class);
    }

    public String getHexAccountState() {
        return Hex.encodeHexString(RLPCodec.encode(this));
    }

    public byte[] getKey() {
        if (account != null) {
            return account.getPubkeyHash();
        }
        return null;
    }

    public long getNonce() {
        return account.getNonce();
    }

    public void setNonce(long nonce) {
        account.setNonce(nonce);
    }

    public static AccountState emptyWASMAccount(byte[] pkHash, byte[] codeHash) {
        byte[] emptyTrieRoot = Trie.<byte[], byte[]>builder()
                .hashFunction(HashUtil::keccak256)
                .store(new ByteArrayMapStore<>())
                .keyCodec(Codec.identity())
                .valueCodec(Codec.identity())
                .build().getNullHash();
        WASMContract empty = new WASMContract(codeHash, emptyTrieRoot);
        return new AccountState(
                new Account(0, pkHash, 0, 0, 0, 0, 0),
                Collections.emptyMap(),
                Collections.emptyMap(),
                6,
                RLPCodec.encode(empty),
                Collections.emptyMap()
        );
    }

    public void setStorageRoot(byte[] storageRoot) {
        WASMContract c = getWASMContract();
        c.setStorageRoot(storageRoot);
        this.Contract = RLPCodec.encode(c);
    }

    public byte[] getStorageRoot() {
        return getWASMContract().getStorageRoot();
    }

    public Map<byte[], Long> getQuotaMap() {
        return this.account.getQuotaMap();
    }

    public void setQuotaMap(Map<byte[], Long> map) {
        this.account.setQuotaMap(map);
    }

    public long getBalance() {
        return account.getBalance();
    }

    public void setBalance(long balance) {
        account.setBalance(balance);
    }

    public byte[] getPubkeyHash() {
        return account.getPubkeyHash();
    }

    public void setPubkeyHash(byte[] pubkeyHash) {
        account.setPubkeyHash(pubkeyHash);
    }

    public byte[] getContractHash() {
        return getWASMContract().getContractHash();
    }

    public void addBalance(long amount) {
        setBalance(SafeMath.add(getBalance(), amount));
    }

    public void subBalance(long amount) {
        setBalance(SafeMath.sub(getBalance(), amount));
    }

    public void addIncubatecost(long amount) {
        this.account.setIncubatecost(SafeMath.add(this.account.getIncubatecost(), amount));
    }

    public long getBlockHeight() {
        return account.getBlockHeight();
    }

    public void setBlockHeight(long blockHeight) {
        account.setBlockHeight(blockHeight);
    }

    public long getVote() {
        return account.getVote();
    }

    public void setVote(long vote) {
        account.setVote(vote);
    }

    public void addVote(long vote) {
        setVote(SafeMath.add(getVote(), vote));
    }

    public void subVote(long vote) {
        setVote(SafeMath.sub(getVote(), vote));
    }

    public long getMortgage() {
        return account.getMortgage();
    }

    public void setMortgage(long mortgage) {
        account.setMortgage(mortgage);
    }

    public void addMortgage(long amount) {
        setMortgage(SafeMath.add(getMortgage(), amount));
    }

    public void subMortgage(long amount) {
        setMortgage(SafeMath.sub(getMortgage(), amount));
    }
}
