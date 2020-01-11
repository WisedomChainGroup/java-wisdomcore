package org.wisdom.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.SneakyThrows;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.account.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
method calls after writing 2w blocks
{
  "getBlock" : 60498,
  "currentHeader" : 3,
  "getLastConfirmedBlock" : 1,
  "getAncestorBlocks" : 168,
  "getCanonicalBlocks" : 1,
  "getCanonicalHeader" : 5,
  "getCanonicalBlock" : 1,
  "getHeader" : 2399082
}
 */
@Component
public class MemoryCachedWisdomBlockChain implements WisdomBlockChain {
    private WisdomBlockChain delegate;

    private static Block TRAP_VALUE = new Block();

    private static final int MAXIMUM_CACHE_SIZE = 256;

    private Cache<HexBytes, Block> blockCache = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .recordStats()
            .build();

    private Cache<HexBytes, Block> headerCache = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .recordStats()
            .build();

    private Cache<HexBytes, Boolean> hasBlockCache = Caffeine
            .newBuilder()
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .recordStats()
            .build();

    private Block currentHeader;

    private Block currentBlock;

    private Block lastConfirmed;

    private ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public MemoryCachedWisdomBlockChain(
            JdbcTemplate tmpl,
            TransactionTemplate txTmpl,
            Block genesis,
            ApplicationContext ctx,
            @Value("${spring.datasource.username}") String databaseUserName,
            @Value("${clear-data}") boolean clearData,
            BasicDataSource basicDataSource) throws Exception {
        this.delegate = new RDBMSBlockChainImpl(tmpl, txTmpl, genesis, ctx, databaseUserName, clearData, basicDataSource);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    try {
                        System.out.println(objectMapper.writeValueAsString(callsCounter));
                    } catch (Exception ignored) {

                    }
                    // 打印缓存命中率
                    System.out.println("block cache stats");
                    System.out.println(blockCache.stats());

                    System.out.println("header cache stats");
                    System.out.println(headerCache.stats());

                    System.out.println("has block cache stats");
                    System.out.println(hasBlockCache.stats());
                }, 0, 5, TimeUnit.SECONDS);
    }

    // count method calls
    public Map<String, Long> callsCounter = new HashMap<>();

    private void incCounter(String method) {
        callsCounter.put(method, callsCounter.getOrDefault(method, 0L) + 1);
    }

    private void clearCache(byte[] hash) {
        currentHeader = null;
        currentBlock = null;
        lastConfirmed = null;
        hasBlockCache.asMap().remove(HexBytes.fromBytes(hash));
        blockCache.asMap().remove(HexBytes.fromBytes(hash));
        headerCache.asMap().remove(HexBytes.fromBytes(hash));
    }

    @Override
    public Block getGenesis() {
        return delegate.getGenesis();
    }

    @Override
    public boolean hasBlock(byte[] hash) {
        incCounter("hasBlock");
        return hasBlockCache.get(HexBytes.fromBytes(hash), (x) -> delegate.hasBlock(x.getBytes()));
    }

    @Override
    public Block currentHeader() {
        incCounter("currentHeader");
        if (currentHeader != null) return currentHeader;
        currentHeader = delegate.currentHeader();
        return currentHeader;
    }

    @Override
    public Block currentBlock() {
        incCounter("currentBlock");
        if (currentBlock != null) return currentBlock;
        currentBlock = delegate.currentBlock();
        return currentBlock;
    }

    @Override
    public Block getHeader(byte[] blockHash) {
        incCounter("getHeader");
        Block h = headerCache.get(HexBytes.fromBytes(blockHash), (x) -> {
            Block header = delegate.getHeader(x.getBytes());
            return header == null ? TRAP_VALUE : header;
        });
        return h == TRAP_VALUE ? null : h;
    }

    @Override
    @SneakyThrows
    public Block getBlock(byte[] blockHash) {
        incCounter("getBlock");
        Block b = blockCache.get(HexBytes.fromBytes(blockHash), (x) -> {
            Block block = delegate.getBlock(x.getBytes());
            return block == null ? TRAP_VALUE : block;
        });
        return b == TRAP_VALUE ? null : b;
    }

    @Override
    public List<Block> getHeaders(long startHeight, int headersCount) {
        incCounter("getHeaders");
        return delegate.getHeaders(startHeight, headersCount);
    }

    @Override
    public List<Block> getBlocks(long startHeight, int headersCount) {
        incCounter("getBlocks long int");
        return delegate.getBlocks(startHeight, headersCount);
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight) {
        incCounter("getBlocks long long");
        return delegate.getBlocks(startHeight, stopHeight);
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit) {
        incCounter("getBlocks long long int");
        return delegate.getBlocks(startHeight, stopHeight, sizeLimit);
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        incCounter("getBlocks long long int boolean");
        return delegate.getBlocks(startHeight, stopHeight, sizeLimit, clipInitial);
    }

    @Override
    public Block getCanonicalHeader(long height) {
        incCounter("getCanonicalHeader");
        return delegate.getCanonicalHeader(height);
    }

    @Override
    public List<Block> getCanonicalHeaders(long startHeight, int headersCount) {
        incCounter("getCanonicalHeaders long int");
        return delegate.getCanonicalHeaders(startHeight, headersCount);
    }

    @Override
    public Block getCanonicalBlock(long height) {
        incCounter("getCanonicalBlock");
        return delegate.getCanonicalBlock(height);
    }

    @Override
    public List<Block> getCanonicalBlocks(long startHeight, int headersCount) {
        incCounter("getCanonicalBlocks");
        return delegate.getCanonicalBlocks(startHeight, headersCount);
    }

    @Override
    public boolean isCanonical(byte[] hash) {
        incCounter("isCanonical");
        return delegate.isCanonical(hash);
    }

    @Override
    public boolean writeBlock(Block block) {
        boolean ret = delegate.writeBlock(block);
        clearCache(block.getHash());
        if (ret) {
            headerCache.put(HexBytes.fromBytes(block.getHash()), block);
            blockCache.put(HexBytes.fromBytes(block.getHash()), block);
            hasBlockCache.put(HexBytes.fromBytes(block.getHash()), true);
        }
        return ret;
    }

    @Override
    public Block getAncestorHeader(byte[] bhash, long anum) {
        incCounter("getAncestorHeader");
        return delegate.getAncestorHeader(bhash, anum);
    }

    @Override
    public Block getAncestorBlock(byte[] bhash, long anum) {
        incCounter("getAncestorBlock");
        return delegate.getAncestorBlock(bhash, anum);
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] bhash, long anum) {
        incCounter("getAncestorHeaders");
        return delegate.getAncestorHeaders(bhash, anum);
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] bhash, long anum) {
        incCounter("getAncestorBlocks");
        return delegate.getAncestorBlocks(bhash, anum);
    }

    @Override
    public long getCurrentTotalWeight() {
        incCounter("getCurrentTotalWeight");
        return delegate.getCurrentTotalWeight();
    }

    @Override
    public boolean hasTransaction(byte[] txHash) {
        incCounter("hasTransaction");
        return delegate.hasTransaction(txHash);
    }

    @Override
    public boolean hasPayload(int type, byte[] payload) {
        incCounter("hasPayload");
        return delegate.hasPayload(type, payload);
    }

    @Override
    public Transaction getTransaction(byte[] txHash) {
        incCounter("getTransaction");
        return delegate.getTransaction(txHash);
    }

    @Override
    public Transaction getTransactionByTo(byte[] pubKeyHash) {
        incCounter("getTransactionByTo");
        return delegate.getTransactionByTo(pubKeyHash);
    }

    @Override
    public List<Transaction> getTransactionsByFrom(byte[] publicKey, int offset, int limit) {
        return delegate.getTransactionsByFrom(publicKey, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] publicKey, int offset, int limit) {
        return delegate.getTransactionsByTypeAndFrom(type, publicKey, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTo(byte[] publicKeyHash, int offset, int limit) {
        return delegate.getTransactionsByTo(publicKeyHash, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] to, int offset, int limit) {
        return delegate.getTransactionsByTypeAndTo(type, to, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit) {
        return delegate.getTransactionsByFromAndTo(from, to, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] from, byte[] to, int offset, int limit) {
        return delegate.getTransactionsByTypeFromAndTo(type, from, to, offset, limit);
    }

    @Override
    public Block getLastConfirmedBlock() {
        incCounter("getLastConfirmedBlock");
        if (lastConfirmed != null) return lastConfirmed;
        lastConfirmed = delegate.getLastConfirmedBlock();
        return lastConfirmed;
    }

    @Override
    public long countBlocksAfter(long timestamp) {
        incCounter("countBlocksAfter");
        return delegate.countBlocksAfter(timestamp);
    }


}
