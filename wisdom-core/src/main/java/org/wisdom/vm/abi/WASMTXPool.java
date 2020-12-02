package org.wisdom.vm.abi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.*;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.controller.WebSocket;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * WASM 智能合约事务不能跳 nonce
 */
@Component
public class WASMTXPool {
    private static final int LOCK_TIMEOUT = 3;
    private static final int EXPIRED_IN = 3600;
    private final TreeSet<TransactionInfo> cache;

    private final Map<HexBytes, TransactionInfo> mCache;


    private final ScheduledExecutorService poolExecutor;


    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    // dropped transactions
    private final Cache<HexBytes, DropInfo> dropped;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DropInfo {
        private Transaction transaction;
        private String reason;
    }


    @RequiredArgsConstructor
    static class TransactionInfo implements Comparable<TransactionInfo> {
        private final long receivedAt;
        private final Transaction tx;

        @Override
        public int compareTo(TransactionInfo o) {
            int cmp = FastByteComparisons.compareTo(tx.from, 0, tx.from.length, o.tx.from, 0, o.tx.from.length);
            if (cmp != 0) return cmp;
            cmp = Long.compare(tx.nonce, o.tx.nonce);
            if (cmp != 0) return cmp;
            cmp = -Long.compare(tx.gasPrice, o.tx.gasPrice);
            if (cmp != 0) return cmp;
            return FastByteComparisons.compareTo(tx.getHash(), 0, tx.getHash().length, o.tx.getHash(), 0, o.tx.getHash().length);
        }
    }

    public WASMTXPool() {
        cache = new TreeSet<>();
        poolExecutor = Executors.newSingleThreadScheduledExecutor();
        dropped = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRED_IN, TimeUnit.SECONDS)
                .build();
        this.mCache = new HashMap<>();
        poolExecutor.scheduleWithFixedDelay(() -> {
            try {
                this.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, EXPIRED_IN, TimeUnit.SECONDS);
    }

    @SneakyThrows
    private void clear() {
        long now = System.currentTimeMillis();
        if (!this.cacheLock.writeLock().tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
            return;
        }
        try {
            List<Transaction> toDrop = new ArrayList<>();
            mCache.values().forEach(i -> {
                if((now - i.receivedAt) / 1000 > EXPIRED_IN){
                    toDrop.add(i.tx);
                }
            });
            toDrop.forEach(t -> this.dropInternal(t, "invalid nonce or timeout"));
        } finally {
            this.cacheLock.writeLock().unlock();
        }
    }

    @SneakyThrows
    public List<String> collect(Collection<? extends Transaction> transactions) {
        List<String> errors = new ArrayList<>();
        this.cacheLock.writeLock().lock();
        try {
            for (Transaction transaction : transactions) {
                TransactionInfo info = new TransactionInfo(System.currentTimeMillis(), transaction);
                if (cache.contains(info))
                    continue;
                cache.add(info);
                mCache.put(HexBytes.fromBytes(info.tx.getHash()), info);
            }
        } finally {
            this.cacheLock.writeLock().unlock();
        }
        return errors;
    }

    private void dropInternal(Transaction tx, String reason) {
        TransactionInfo info = this.mCache.get(HexBytes.fromBytes(tx.getHash()));
        if (info != null){
            cache.remove(info);
        }
        mCache.remove(HexBytes.fromBytes(tx.getHash()));
        dropped.asMap().put(HexBytes.fromBytes(tx.getHash()),
                new DropInfo(tx, reason));
        WebSocket.broadcastDrop(tx, reason);
    }


    public void drop(Transaction tx, String reason) {
        this.cacheLock.writeLock().lock();
        try {
            this.dropInternal(tx, reason);
        } finally {
            this.cacheLock.writeLock().unlock();
        }
    }

    @SneakyThrows
    public List<Transaction> popPackable(Map<byte[], Long> m, int limit) {
        this.cacheLock.writeLock().lock();
        try {
            Map<byte[], Long> nonceMap = new ByteArrayMap<>();

            Iterator<TransactionInfo> it = cache.iterator();
            List<Transaction> ret = new ArrayList<>();
            int count = 0;
            while (count < ((limit < 0) ? Long.MAX_VALUE : limit) && it.hasNext()) {
                Transaction t = it.next().tx;
                long prevNonce =
                        nonceMap.containsKey(t.getFromPKHash()) ?
                                nonceMap.get(t.getFromPKHash()) :
                                m.getOrDefault(t.getFromPKHash(), 0L);

                if (t.nonce <= prevNonce) {
                    it.remove();
                    mCache.remove(HexBytes.fromBytes(t.getHash()));
                    dropped.put(HexBytes.fromBytes(t.getHash()), new DropInfo(t, "nonce too small"));
                    WebSocket.broadcastDrop(t, "invalid nonce: too small");
                    continue;
                }
                if (t.nonce != prevNonce + 1) {
                    continue;
                }
                nonceMap.put(t.getFromPKHash(), t.nonce);
                ret.add(t);
                it.remove();
                count++;
            }
            // 对于 from 相同的事务，优先取 nonce 较小的
            // 对于 from 不相同的事务，优先取 gasPrice 较大的
            ret.sort((x, y) -> {
                if (!FastByteComparisons.equal(x.from, y.from)) {
                    return -Long.compare(x.gasPrice, y.gasPrice);
                }
                return Long.compare(x.nonce, y.nonce);
            });
            return ret;
        } finally {
            this.cacheLock.writeLock().unlock();
        }
    }

    public int size() {
        return cache.size();
    }

    @SneakyThrows
    public void onNewBestBlock(Block block) {
        if (!this.cacheLock.writeLock().tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
            return;
        }
        try {
            block.body.forEach(t ->
                    {
                        cache.remove(new TransactionInfo(System.currentTimeMillis(), t));
                        mCache.remove(HexBytes.fromBytes(t.getHash()));
                    }
            );
        } finally {
            this.cacheLock.writeLock().unlock();
        }
    }

    public Optional<Object> get(HexBytes hash) {
        this.cacheLock.readLock().lock();
        try {
            DropInfo i = dropped.asMap().get(hash);
            if(i != null)
                return Optional.of(i);
            TransactionInfo info = mCache.get(hash);
            if(info != null)
                return Optional.of(info);
            return Optional.empty();
        } finally {
            this.cacheLock.readLock().unlock();
        }
    }

    public List<HexBytes> getTransactionHashes(){
        return new ArrayList<>(this.mCache.keySet());
    }
}
