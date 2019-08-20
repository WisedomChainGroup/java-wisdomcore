package org.wisdom.db;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;

import java.util.*;

@Component
public class StateDB {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final int CACHE_SIZE = 16;

    @Autowired
    private WisdomBlockChain bc;

    public StateDB() {
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, Account>>()
                .maximumWeightedCapacity(CACHE_SIZE).build();
    }

    // 用于记录每个未确认区块相对于持久化的账本产生状态变更的账户
    private Map<String, Map<String, Account>> cache;

    // 最新已确认的区块
    private Block latestConfirmed;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }

    // 或取到某一区块（包含该区块)的某个账户的状态，用于对后续区块的事务进行验证
    public Account getAccount(byte[] blockHash, byte[] publicKeyHash) {
        if (!bc.hasBlock(blockHash)) {
            return null;
        }
        if (Arrays.equals(blockHash, latestConfirmed.getHash())) {
            return getAccount(publicKeyHash);
        }
        // 判断新的区块是否在 main fork 上面
        Block ancestor = bc.findAncestorHeader(blockHash, latestConfirmed.nHeight);
        if (!Arrays.equals(latestConfirmed.getHash(), ancestor.getHash())) {
            return null;
        }
        // 判断是否在缓存中
        String blockKey = getLRUCacheKey(blockHash);
        String accountKey = getLRUCacheKey(publicKeyHash);
        if (cache.containsKey(blockKey) && cache.get(blockKey).containsKey(accountKey)) {
            return cache.get(blockKey).get(accountKey);
        }
        // 如果不存在则进行回溯
        Block block = bc.getBlock(blockHash);
        Account account = getAccount(block.hashPrevBlock, publicKeyHash);
        if (account == null) {
            return null;
        }
        // 查看是否需要对这账户进行更新
        // 把这个区块的事务应用到 account 中
        Account res = applyTransactions(block.body, account);
        if (!cache.containsKey(blockKey)) {
            cache.put(blockKey, new HashMap<>());
        }
        cache.get(blockKey).put(accountKey, res);
        return res;
    }

    // 获取已经持久化的账户列表
    public Account getAccount(byte[] publicKeyHash) {
        return null;
    }

    // 将缓存的账户更新到数据库中
    public void updateAccounts(byte[] blockHash, Account... accounts) {

    }

    public Account applyTransactions(List<Transaction> txs, Account account) {
        return null;
    }
}
