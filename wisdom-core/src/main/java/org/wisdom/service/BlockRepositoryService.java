package org.wisdom.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.dao.HeaderDao;
import org.wisdom.dao.TransactionDao;
import org.wisdom.dao.TransactionDaoJoined;
import org.wisdom.dao.TransactionIndexDao;
import org.wisdom.entity.HeaderEntity;
import org.wisdom.entity.Mapping;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BlockRepositoryService implements WisdomBlockChain {

    private HeaderDao headerDao;

    private TransactionDao transactionDao;

    private TransactionIndexDao transactionIndexDao;

    private TransactionDaoJoined transactionDaoJoined;

    private Block genesis;

    public BlockRepositoryService(HeaderDao headerDao,
                                  TransactionDao transactionDao,
                                  TransactionIndexDao transactionIndexDao,
                                  TransactionDaoJoined transactionDaoJoined,
                                  Block genesis, boolean clearData) throws Exception {
        this.transactionDao = transactionDao;
        this.headerDao = headerDao;
        this.transactionIndexDao = transactionIndexDao;
        this.transactionDaoJoined = transactionDaoJoined;
        this.genesis = genesis;

        if (clearData) {
            clearData();
        }

        if (getHeaderByHeight(0) == null) {
            writeBlock(genesis);
        }

        if (!FastByteComparisons.equal(getGenesisInternal().getHash(), genesis.getHash())) {
            throw new Exception("the genesis in db and genesis in config is not equal");
        }

    }

    private void clearData() {
        transactionIndexDao.deleteAllInBatch();
        transactionDao.deleteAllInBatch();
        headerDao.deleteAllInBatch();
    }

    private Block setBody(Block header) {
        if (header == null) return null;
        header.body = transactionDaoJoined.getTransactionsByBlockHash(header.getHash());
        return header;
    }

    private List<Block> setBodies(@NonNull List<Block> headers) {
        if (headers.isEmpty()) return headers;
        List<Transaction> all = transactionDaoJoined
                .getTransactionsByBlockHashIn(headers.stream().map(Block::getHash).collect(Collectors.toList()));

        Map<byte[], Block> cache = new ByteArrayMap<>();
        for (Block b : headers) {
            cache.put(b.getHash(), b);
            b.body = new ArrayList<>();
        }

        for (Transaction tx : all) {
            cache.get(tx.blockHash).body.add(tx);
        }
        return cache.values().stream().sorted(Comparator.comparingLong(x -> x.nHeight)).collect(Collectors.toList());
    }

    @Override
    public Block getGenesis() {
        return genesis;
    }

    private Block getGenesisInternal() {
        return getBlockByHeight(0);
    }

    @Override
    public boolean containsBlock(byte[] hash) {
        return headerDao.existsByBlockHash(hash);
    }

    @Override
    public Block getTopHeader() {
        return headerDao.findTopByOrderByHeightDesc()
                .map(Mapping::getHeaderFromEntity)
                .orElse(null);

    }

    @Override
    public Block getTopBlock() {
        return setBody(getTopHeader());
    }

    @Override
    public Block getHeaderByHash(byte[] blockHash) {
        return Mapping.getHeaderFromEntity(headerDao.findByBlockHash(blockHash));
    }

    @Override
    public Block getBlockByHash(byte[] blockHash) {
        HeaderEntity headerEntity = headerDao.findByBlockHash(blockHash);
        return setBody(Mapping.getHeaderFromEntity(headerEntity));
    }

    @Override
    public List<Block> getHeadersSince(long startHeight, int headersCount) {
        List<HeaderEntity> headerEntity = headerDao.findByHeightBetweenOrderByHeight(startHeight, startHeight + headersCount - 1);
        return headerEntity.stream().map(Mapping::getHeaderFromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Block> getBlocksSince(long startHeight, int headersCount) {
        List<Block> headers = getHeadersSince(startHeight, headersCount);
        return setBodies(headers);
    }

    @Override
    public List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (sizeLimit == 0) return new ArrayList<>();
        if (sizeLimit < 0) sizeLimit = Integer.MAX_VALUE;
        List<HeaderEntity> headerEntity;
        if (clipInitial) {
            headerEntity = headerDao.findByHeightBetweenOrderByHeight(startHeight, stopHeight,
                    PageRequest.of(0, sizeLimit, Sort.Direction.DESC, "height"));
        } else {
            headerEntity = headerDao.findByHeightBetweenOrderByHeight(startHeight, stopHeight,
                    PageRequest.of(0, sizeLimit, Sort.Direction.ASC, "height"));
        }
        return headerEntity.stream().map(Mapping::getHeaderFromEntity).collect(Collectors.toList());
    }


    @Override
    public List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return setBodies(getHeadersBetween(startHeight, stopHeight, sizeLimit, clipInitial));
    }

    @Override
    public Block getHeaderByHeight(long height) {
        return headerDao.findByHeight(height)
                .map(Mapping::getHeaderFromEntity)
                .orElse(null);
    }

    @Override
    public Block getBlockByHeight(long height) {
        return setBody(getHeaderByHeight(height));
    }

    @Override
    public boolean writeBlock(Block block) {
        try {
            headerDao.save(Mapping.getEntityFromHeader(block));
            transactionDao.saveAll(Mapping.getEntitiesFromTransactions(block));
            transactionIndexDao.saveAll(Mapping.getTransactionIndexEntitiesFromBlock(block));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to write block, height is" + block.getnHeight());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] hash, long ancestorHeight) {
        HeaderEntity headerEntity = headerDao.findByBlockHash(hash);
        if (headerEntity == null) {
            return Collections.emptyList();
        }
        return headerDao.findByHeightBetweenOrderByHeight(ancestorHeight, headerEntity.height).stream()
                .map(Mapping::getHeaderFromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] hash, long ancestorHeight) {
        return setBodies(getAncestorHeaders(hash, ancestorHeight));
    }

    @Override
    public long getTopHeight() {
        return headerDao.findTopByOrderByHeightDesc()
                .map(h -> h.height)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public boolean containsTransaction(byte[] txHash) {
        return transactionDao.existsByTxHash(txHash);
    }

    @Override
    public boolean containsPayload(int type, byte[] payload) {
        return transactionDao.existsByTypeAndPayload(type, payload);
    }

    @Override
    public Transaction getTransaction(byte[] txHash) {
        return Mapping.getTransactionFromEntity(transactionDao.findByTxHash(txHash));
    }

    @Override
    public Transaction getTransactionByTo(byte[] to) {
        return transactionDaoJoined.getTransactionByTo(to);
    }

    @Override
    public List<Transaction> getTransactionsByFrom(byte[] from, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByFrom(from, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] from, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByTypeAndFrom(type, from, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTo(byte[] to, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByTo(to, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] to, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByTypeAndTo(type, to, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByFromAndTo(from, to, offset, limit);
    }

    @Override
    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] from, byte[] to, int offset, int limit) {
        return transactionDaoJoined.getTransactionsByTypeFromAndTo(type, from, to, offset, limit);
    }

    @Override
    public long countBlocksAfter(long createdAt) {
        return headerDao.countByCreatedAtAfter(createdAt);
    }
}
