package org.wisdom.entity;


import lombok.NonNull;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    public static Block getHeaderFromEntity(HeaderEntity entity) {
        if (entity == null) return null;
        Block header = new Block();
        header.nVersion = entity.getVersion();
        header.hashPrevBlock = entity.getHashPrevBlock();
        header.hashMerkleRoot = entity.getHashMerkleRoot();
        header.hashMerkleState = entity.getHashMerkleState();
        header.hashMerkleIncubate = entity.getHashMerkleIncubate();
        header.nHeight = entity.getHeight();
        header.nTime = entity.getCreatedAt();
        header.nNonce = entity.getNNonce();
        header.nBits = entity.getNBits();
        header.blockNotice = entity.getBlockNotice();
        header.totalWeight = entity.getTotalWeight();
        return header;
    }

    public static HeaderEntity getEntityFromHeader(Block block) {
        return HeaderEntity.builder()
                .blockHash(block.getHash())
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
                .canonical(true)
                .version(block.nVersion)
                .build();
    }

    public static Transaction getTransactionFromEntity(@NonNull TransactionEntity entity) {
        return new Transaction(
                entity.version, entity.type, entity.nonce,
                entity.from, entity.gasPrice, entity.amount,
                entity.payload, entity.to, entity.signature
        );
    }

    public static TransactionEntity getEntityFromTransaction(@NonNull Transaction tx) {
        return TransactionEntity.builder()
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
                .build();
    }

    public static List<TransactionEntity> getEntitiesFromTransactions(Block block) {
        return block.body.stream().map(Mapping::getEntityFromTransaction).collect(Collectors.toList());
    }

    public static List<TransactionIndexEntity> getTransactionIndexEntitiesFromBlock(Block block) {
        List<TransactionIndexEntity> entities = new ArrayList<>(block.body.size());
        for (int i = 0; i < block.body.size(); i++) {
            entities.add(
                    TransactionIndexEntity.builder().blockHash(block.getHash())
                            .txHash(block.body.get(i).getHash()).txIndex(i).build()
            );
        }
        return entities;
    }

}
