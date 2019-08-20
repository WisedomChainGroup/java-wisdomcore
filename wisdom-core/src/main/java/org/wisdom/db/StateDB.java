package org.wisdom.db;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.BlockChainOptional;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class StateDB {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final int CACHE_SIZE = 16;

    @Autowired
    private WisdomBlockChain bc;

    private ReadWriteLock readWriteLock;

    public StateDB() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, Account>>()
                .maximumWeightedCapacity(CACHE_SIZE).build();
    }

    // 区块相对于已持久化的账本产生状态变更的账户
    private Map<String, Map<String, Account>> cache;

    // 最新确认的区块
    private Block latestConfirmed;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }

    public List<Account> getAccounts(byte[] blockHash, List<byte[]> publicKeyHashes) {
        readWriteLock.readLock().lock();
        List<Account> result = new ArrayList<>();
        for (byte[] h : publicKeyHashes) {
            Account account = getAccountUnsafe(blockHash, h);
            if (account == null) {
                return null;
            }
            result.add(account);
        }
        readWriteLock.readLock().unlock();
        return result;
    }

    // 或取到某一区块（包含该区块)的某个账户的状态，用于对后续区块的事务进行验证
    private Account getAccountUnsafe(byte[] blockHash, byte[] publicKeyHash) {
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
        Account account = getAccountUnsafe(header.hashPrevBlock, publicKeyHash);
        if (account == null) {
            return null;
        }
        // 查看是否需要对这账户进行更新
        Block block = bc.getBlock(blockHash);
        // 不需要则 return

        // 如果需要则把这个区块的事务应用到上一个区块获取的 account，生成新的 account
        Account res = applyTransactions(block.body, account);
        if (!cache.containsKey(blockKey)) {
            cache.put(blockKey, new ConcurrentHashMap<>());
        }
        cache.get(blockKey).put(accountKey, res);
        return res;
    }

    // 获取已经持久化的账户
    private Account getAccount(byte[] publicKeyHash) {
        return null;
    }

    // 将缓存的账户更新到数据库中
    public void updateAccounts(byte[] blockHash, Account... accounts) {
        readWriteLock.writeLock().lock();
        // 数据库读写
        // 成功则更新 lastConfirmed
        readWriteLock.writeLock().unlock();
    }

    public Account applyTransaction(Transaction tx, Account account) {
        return null;
    }

    public Account applyTransactions(List<Transaction> txs, Account account) {
        return null;
    }
}
