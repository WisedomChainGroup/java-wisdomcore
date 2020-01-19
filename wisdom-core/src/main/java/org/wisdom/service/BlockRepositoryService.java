package org.wisdom.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
import org.wisdom.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BlockRepositoryService implements WisdomBlockChain {

    private HeaderDao headerDao;

    private TransactionDao transactionDao;

    private TransactionIndexDao transactionIndexDao;

    private TransactionDaoJoined transactionDaoJoined;

    public BlockRepositoryService(HeaderDao headerDao,
                                  TransactionDao transactionDao,
                                  TransactionIndexDao transactionIndexDao,
                                  TransactionDaoJoined transactionDaoJoined,
                                  Block genesis, boolean clearData) throws Exception {
        this.transactionDao = transactionDao;
        this.headerDao = headerDao;
        this.transactionIndexDao = transactionIndexDao;
        this.transactionDaoJoined = transactionDaoJoined;
        if (!FastByteComparisons.equal(getGenesis().getHash(), genesis.getHash())) {
            throw new Exception("the genesis in db and genesis in config is not equal");
        }
        if (clearData) {
            clearData();
        }
    }

    private void clearData() {
        headerDao.deleteAllInBatch();
        transactionIndexDao.deleteAllInBatch();
        transactionDao.deleteAllInBatch();
    }

    private Block setBody(Block header){
        header.body = transactionDaoJoined.getTransactionsByBlockHash(header.getHash());
        return header;
    }

    @Override
    public Block getGenesis() {
        HeaderEntity headerEntity = headerDao.findByHeight(0L);
        List<TransactionEntity> transactionEntities = transactionIndexDao.findByBlockHash(headerEntity.blockHash)
                .stream().map(x -> transactionDao.findByTxHash(x.txHash)).collect(Collectors.toList());
        return Mapping.getBlockFromHeaderEntity(headerEntity, transactionEntities);
    }

    @Override
    public boolean containsBlock(byte[] hash) {
        return headerDao.existsByBlockHash(hash);
    }

    @Override
    public Block getTopHeader() {
        return Mapping.getHeaderFromHeaderEntity(headerDao.findTopByOrderByHeightDesc().get());
    }

    @Override
    public Block getTopBlock() {
        HeaderEntity headerEntity = headerDao.findTopByOrderByHeightDesc().get();
        List<TransactionEntity> transactionEntities = transactionIndexDao.findByBlockHash(headerEntity.blockHash)
                .stream().map(x -> transactionDao.findByTxHash(x.txHash)).collect(Collectors.toList());
        return Mapping.getBlockFromHeaderEntity(headerEntity, transactionEntities);
    }

    @Override
    public Block getHeaderByHash(byte[] blockHash) {
        return Mapping.getHeaderFromHeaderEntity(headerDao.findByBlockHash(blockHash));
    }

    @Override
    public Block getBlockByHash(byte[] blockHash) {
        HeaderEntity headerEntity = headerDao.findByBlockHash(blockHash);
        if (headerEntity == null){
            return null;
        }
        return setBody(Mapping.getHeaderFromHeaderEntity(headerEntity));
    }

    @Override
    public List<Block> getHeadersSince(long startHeight, int headersCount) {
        List<HeaderEntity> headerEntity = headerDao.findByHeightBetweenOrderByHeight(startHeight, startHeight + headersCount - 1);
        return headerEntity.stream().map(Mapping::getHeaderFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Block> getBlocksSince(long startHeight, int headersCount) {
        List<HeaderEntity> headerEntity = headerDao.findByHeightBetweenOrderByHeight(startHeight, startHeight + headersCount - 1);
        return getBlocks(headerEntity);
    }

    @Override
    public List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (sizeLimit == 0) return new ArrayList<>();
        if (sizeLimit < 0) sizeLimit = Integer.MAX_VALUE;
        List<HeaderEntity> headerEntity;
        if (clipInitial) {
            headerEntity = headerDao.findByHeightBetweenOrderByHeightDesc(startHeight, stopHeight, PageRequest.of(0, sizeLimit));
        } else {
            headerEntity = headerDao.findByHeightBetweenOrderByHeightAsc(startHeight, stopHeight, PageRequest.of(0, sizeLimit));
        }
        return headerEntity.stream().map(Mapping::getHeaderFromHeaderEntity).collect(Collectors.toList());
    }

    private List<Block> getBlocks(List<HeaderEntity> headerEntity) {
        List<Block> list = new ArrayList<>();
        headerEntity.forEach(x -> {
            List<TransactionEntity> transactionEntities =
                    transactionIndexDao.findByBlockHash(x.blockHash)
                            .stream().map(tx -> transactionDao.findByTxHash(tx.txHash))
                            .collect(Collectors.toList());
            list.add(Mapping.getBlockFromHeaderEntity(x, transactionEntities));
        });
        return list;
    }

    @Override
    public List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (sizeLimit == 0) return new ArrayList<>();
        if (sizeLimit < 0) sizeLimit = Integer.MAX_VALUE;
        if (clipInitial) {
            List<HeaderEntity> headerEntity = headerDao.findByHeightBetweenOrderByHeightDesc(startHeight, stopHeight, PageRequest.of(0, sizeLimit));
            return getBlocks(headerEntity);
        }
        List<HeaderEntity> headerEntity = headerDao.findByHeightBetweenOrderByHeightAsc(startHeight, stopHeight, PageRequest.of(0, sizeLimit));
        return getBlocks(headerEntity);
    }

    @Override
    public Block getHeaderByHeight(long height) {
        return Mapping.getHeaderFromHeaderEntity(headerDao.findByHeight(height));
    }

    @Override
    public Block getBlockByHeight(long height) {
        HeaderEntity headerEntity = headerDao.findByHeight(height);
        List<TransactionEntity> transactionEntities = transactionIndexDao.findByBlockHash(headerEntity.blockHash)
                .stream().map(x -> transactionDao.findByTxHash(x.txHash)).collect(Collectors.toList());
        return Mapping.getBlockFromHeaderEntity(headerEntity, transactionEntities);
    }

    @Override
    public boolean writeBlock(Block block) {
        try {
            headerDao.save(Mapping.getEntityFromHeader(block));
            List<TransactionEntity> entities= Mapping.getTransactionEntitiesFromBlock(block);
            transactionDao.saveAll(entities);
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
                .map(Mapping::getHeaderFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] hash, long ancestorHeight) {
        HeaderEntity headerEntity = headerDao.findByBlockHash(hash);
        if (headerEntity == null) {
            return Collections.emptyList();
        }
        return getBlocks(headerDao.findByHeightBetweenOrderByHeight(ancestorHeight, headerEntity.height));
    }

    @Override
    public long getTopHeight() {
        return headerDao.findTopByOrderByHeightDesc().get().height;
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
        return Mapping.getTransactionFromHeaderEntity(transactionDao.findByTxHash(txHash));
    }

    @Override
    public Transaction getTransactionByTo(byte[] to) {
        return Mapping.getTransactionFromHeaderEntity(transactionDao.findByTo(to, PageRequest.of(0, 1))
                .stream().findFirst().get());
    }

    @Override
    public List<Transaction> getTransactionsByFrom(byte[] from, int offset, int limit) {
        return transactionDao.findByFrom(from, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] from, int offset, int limit) {
        return transactionDao.findByFromAndType(from, type, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByTo(byte[] to, int offset, int limit) {
        return transactionDao.findByTo(to, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] to, int offset, int limit) {
        return transactionDao.findByToAndType(to, type, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit) {
        return transactionDao.findByFromAndTo(from, to, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] from, byte[] to, int offset, int limit) {
        return transactionDao.findByFromAndToAndType(from, to, type, PageRequest.of(offset, limit)).stream()
                .map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
    }

    @Override
    public long countBlocksAfter(long createdAt) {
        return headerDao.findByCreatedAtAfter(createdAt).size();
    }
}
