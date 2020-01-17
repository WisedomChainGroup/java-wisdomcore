package org.wisdom.entity;


import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    public static Block getHeaderFromHeaderEntity(HeaderEntity header) {
        Block block = new Block();
        return getHeader(header, block);
    }

    public static HeaderEntity getEntityFromHeader(Block block) {
        return HeaderEntity.builder().blockHash(block.getHash())
                .blockNotice(block.blockNotice)
                .hashMerkleIncubate(block.hashMerkleIncubate)
                .hashMerkleRoot(block.hashMerkleRoot)
                .hashMerkleState(block.hashMerkleState)
                .hashPrevBlock(block.hashPrevBlock)
                .height(block.nHeight)
                .createdAt(block.nTime)
                .nNonce(block.nNonce)
                .nBits(block.nBits)
                .totalWeight(block.totalWeight)
                .is_canonical(true)
                .version(block.nVersion)
                .build();
    }

    public static Transaction getTransactionFromHeaderEntity(TransactionEntity entity) {
        Transaction transaction = new Transaction();
        transaction.from = entity.from;
        transaction.to = entity.to;
        transaction.amount = entity.amount;
        transaction.type = entity.type;
        transaction.nonce = entity.nonce;
        transaction.gasPrice = entity.gasPrice;
        transaction.payload = entity.payload;
        transaction.version = entity.version;
        transaction.signature = entity.signature;
        return transaction;
    }

    public static Block getBlockFromHeaderEntity(HeaderEntity header, List<TransactionEntity> entities) {
        List<Transaction> txs = entities.stream().map(Mapping::getTransactionFromHeaderEntity).collect(Collectors.toList());
        Block block = new Block();
        block.body = txs;
        return getHeader(header, block);
    }

    private static Block getHeader(HeaderEntity header, Block block) {
        block.setBlockHash(header.blockHash);
        block.blockNotice = header.blockNotice;
        block.nHeight = header.height;
        block.hashMerkleIncubate = header.hashMerkleIncubate;
        block.hashPrevBlock = header.hashPrevBlock;
        block.hashMerkleRoot = header.hashMerkleRoot;
        block.hashMerkleState = header.hashMerkleState;
        block.nBits = header.nBits;
        block.nNonce = header.nNonce;
        block.nTime = header.createdAt;
        block.nVersion = header.version;
        block.totalWeight = header.totalWeight;
        return block;
    }

    public static List<TransactionEntity> getTransactionEntitiesFromBlock(Block block) {
        List<TransactionEntity> entities = new ArrayList<>();
        block.body.forEach(tx -> entities.add(TransactionEntity.builder()
                .amount(tx.amount)
                .from(tx.from)
                .gasPrice(tx.gasPrice)
                .nonce(tx.nonce)
                .payload(tx.payload)
                .signature(tx.signature)
                .to(tx.to)
                .txHash(tx.getHash())
                .type(tx.type)
                .version(tx.version)
                .build()));
        return entities;
    }

    public static List<TransactionIndexEntity> getTransactionIndexEntitiesFromBlock(Block block) {
        List<TransactionIndexEntity> entities = new ArrayList<>();
        for (int i = 0; i < block.body.size(); i++) {
            entities.add(TransactionIndexEntity.builder().blockHash(block.getHash())
                    .txHash(block.body.get(i).getHash()).txIndex(i).build());
        }
        return entities;
    }

}
