package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.command.Configuration;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
public class StateDB {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final int CACHE_SIZE = 16;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private AccountDB accountDB;

    @Autowired
    private IncubatorDB incubatorDB;

    @Autowired
    private RateTable rateTable;

    @Autowired
    private Configuration configuration;

    private ReadWriteLock readWriteLock;

    public StateDB() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, AccountState>>()
                .maximumWeightedCapacity(CACHE_SIZE).build();
    }

    // 区块相对于已持久化的账本产生状态变更的账户
    private Map<String, Map<String, AccountState>> cache;

    // 最新确认的区块
    private Block latestConfirmed;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }

    public Map<String, AccountState> getAccounts(byte[] blockHash, List<byte[]> publicKeyHashes) {
        readWriteLock.readLock().lock();
        Map<String, AccountState> res = null;
        try {
            res = new HashMap<>();
            for (byte[] h : publicKeyHashes) {
                AccountState account = getAccountUnsafe(blockHash, h);
                if (account == null) {
                    return null;
                }
                res.put(Hex.encodeHexString(h), account);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return res;
    }

    // 或取到某一区块（包含该区块)的某个账户的状态，用于对后续区块的事务进行验证
    private AccountState getAccountUnsafe(byte[] blockHash, byte[] publicKeyHash) {
        Block header = bc.getHeader(blockHash);
        if (header == null || header.nHeight < latestConfirmed.nHeight) {
            return null;
        }
        if (Arrays.equals(blockHash, latestConfirmed.getHash())) {
            return getAccount(publicKeyHash);
        }
        // 判断新的区块是否在 main fork 上面
        Block ancestor = bc.findAncestorHeader(blockHash, latestConfirmed.nHeight);
        if (ancestor == null || !Arrays.equals(latestConfirmed.getHash(), ancestor.getHash())) {
            return null;
        }
        // 判断是否在缓存中
        String blockKey = getLRUCacheKey(blockHash);
        String accountKey = getLRUCacheKey(publicKeyHash);
        if (cache.containsKey(blockKey) && cache.get(blockKey).containsKey(accountKey)) {
            return cache.get(blockKey).get(accountKey);
        }
        // 如果缓存不存在则进行回溯
        AccountState account = getAccountUnsafe(header.hashPrevBlock, publicKeyHash);
        if (account == null) {
            return null;
        }
        Block block = bc.getBlock(blockHash);
        // 把这个区块的事务应用到上一个区块获取的 account，生成新的 account
        AccountState res = applyTransactions(block.body, account.copy());
        if (!cache.containsKey(blockKey)) {
            cache.put(blockKey, new ConcurrentHashMap<>());
        }
        cache.get(blockKey).put(accountKey, res);
        return res;
    }

    // 获取已经持久化的账户
    public AccountState getAccount(byte[] publicKeyHash) {
        Account account = accountDB.selectaccount(publicKeyHash);
        if (account == null) {
            return null;
        }
        List<Incubator> incubatorList = incubatorDB.selectList(publicKeyHash);
        List<Incubator> shareincubList = incubatorDB.selectShareList(publicKeyHash);
        Map<String, Incubator> incubatorMap = incubatorList.stream().collect(Collectors.toMap(Incubator::getTxhash, Incubator -> Incubator));
        Map<String, Incubator> shareincubMap = shareincubList.stream().collect(Collectors.toMap(Incubator::getTxhash, Incubator -> Incubator));
        return new AccountState(account, incubatorMap, shareincubMap);
    }

    private AccountState applyCoinbase(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        // 如果该账户不是 coinbase 地址退出
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState applyTransfer(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState applyIncubate(Transaction tx, AccountState accountState) throws Exception {
        Account account = accountState.getAccount();
        byte[] playload = tx.payload;
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
        int days = payloadproto.getType();
        String sharpub = payloadproto.getSharePubkeyHash();
        byte[] share_pubkeyhash = new byte[0];
        if (sharpub != null && !sharpub.equals("")) {
            share_pubkeyhash = Hex.decodeHex(sharpub.toCharArray());
        }
        long balance;
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.getFee();
            balance -= tx.amount;
            long incub = account.getIncubatecost();
            incub += tx.amount;
            account.setBalance(balance);
            account.setIncubatecost(incub);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);

            long interest = tx.getInterest(tx.height, rateTable, days);
            Incubator incubator = new Incubator(tx.to, tx.getHash(), tx.height, tx.amount, interest, tx.height, days);
            Map<String, Incubator> incubatorMap = accountState.getIncubatorMap();
            incubatorMap.put(Hex.encodeHexString(tx.getHash()), incubator);
            accountState.setIncubatorMap(incubatorMap);
        }
        if (Arrays.equals(share_pubkeyhash, account.getPubkeyHash())) {
            long share = tx.getShare(tx.height, rateTable, days);
            Incubator shareincub = new Incubator(share_pubkeyhash, tx.getHash(), tx.height, tx.amount, days, share, tx.height);
            Map<String, Incubator> shareMap = accountState.getShareincubMap();
            shareMap.put(Hex.encodeHexString(tx.getHash()), shareincub);
            accountState.setShareincubMap(shareMap);
        }
        if (Arrays.equals(IncubatorAddress.resultpubhash(), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            long nonce = account.getNonce();
            nonce++;
            account.setBalance(balance);
            account.setNonce(nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState applyExtractInterest(Transaction tx, AccountState accountState) throws Exception {
        Account account = accountState.getAccount();
        long balance;
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);

        String tranhash = Hex.encodeHexString(tx.payload);
        Map<String, Incubator> incubatorMap = accountState.getIncubatorMap();
        if (!incubatorMap.containsKey(tranhash)) {
            throw new Exception("apply extract interest transaction failed: transaction" + tranhash + "not found");
        }
        Incubator incubator = incubatorMap.get(tranhash);
        String rate = rateTable.selectrate(tx.height, incubator.getDays());
        BigDecimal amounbig = BigDecimal.valueOf(tx.amount);
        BigDecimal ratebig = new BigDecimal(rate);
        long dayinterset = ratebig.multiply(amounbig).longValue();
        long lastheight = incubator.getLast_blockheight_interest();
        if (dayinterset > tx.amount) {
            lastheight += configuration.getDay_count();
        } else {
            int extractday = (int) (tx.amount / dayinterset);
            long extractheight = extractday * configuration.getDay_count();
            lastheight += extractheight;
        }
        long lastinterset = incubator.getInterest_amount();
        lastinterset -= tx.amount;
        incubator.setHeight(tx.height);
        incubator.setInterest_amount(lastinterset);
        incubator.setLast_blockheight_interest(lastheight);
        incubatorMap.put(tranhash, incubator);
        accountState.setIncubatorMap(incubatorMap);
        return accountState;
    }

    private AccountState applyExtractSharingProfit(Transaction tx, AccountState accountState) throws Exception {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);

        String tranhash = Hex.encodeHexString(tx.payload);
        Map<String, Incubator> shareMap = accountState.getShareincubMap();
        if (!shareMap.containsKey(tranhash)) {
            throw new Exception("apply extract sharing profit transaction failed: transaction" + tranhash + "not found");
        }
        Incubator incubator = shareMap.get(tranhash);
        String rate = rateTable.selectrate(tx.height, incubator.getDays());
        BigDecimal amounbig = BigDecimal.valueOf(tx.amount);
        BigDecimal ratebig = new BigDecimal(rate);
        BigDecimal onemul = amounbig.multiply(ratebig);
        BigDecimal bl = BigDecimal.valueOf(0.1);
        long dayinterset = onemul.multiply(bl).longValue();
        long lastheight = incubator.getLast_blockheight_share();
        if (dayinterset > tx.amount) {
            lastheight += configuration.getDay_count();
        } else {
            int extractday = (int) (tx.amount / dayinterset);
            long extractheight = extractday * configuration.getDay_count();
            lastheight += extractheight;
        }
        long lastshare = incubator.getShare_amount();
        lastshare -= tx.amount;
        incubator.setHeight(tx.height);
        incubator.setShare_amount(lastshare);
        incubator.setLast_blockheight_share(lastheight);
        shareMap.put(tranhash, incubator);
        accountState.setShareincubMap(shareMap);
        return accountState;
    }

    // 调用前先执行深拷贝
    public AccountState applyTransaction(Transaction tx, AccountState accountState) throws Exception {
        int type = tx.type;
        switch (type) {
            case 0x00:
                return applyCoinbase(tx, accountState);
            case 0x01:
                return applyTransfer(tx, accountState);
            case 0x09:
                return applyIncubate(tx, accountState);
            case 0x0a:
                return applyExtractInterest(tx, accountState);
            case 0x0b:
                return applyExtractSharingProfit(tx, accountState);
            case 0x0c:
                return applyExtractCost(tx, accountState);
            default:
                throw new Exception("unsupported transaction type: " + Transaction.Type.values()[type].toString());
        }
    }

    private AccountState applyExtractCost(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long incub = account.getIncubatecost();
        incub -= tx.amount;
        account.setBalance(balance);
        account.setIncubatecost(incub);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        String tranhash = Hex.encodeHexString(tx.payload);
        Map<String, Incubator> incubatorMap = accountState.getIncubatorMap();
        incubatorMap.remove(tranhash);
        accountState.setIncubatorMap(incubatorMap);
        return accountState;
    }


    public AccountState applyTransactions(List<Transaction> txs, AccountState account) {
        for (Transaction Transaction : txs) {
            try {
                account = applyTransaction(Transaction, account);
                if (account == null) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

        }
        return account;
    }
}
