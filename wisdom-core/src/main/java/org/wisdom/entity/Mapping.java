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

    @Deprecated // use transaction dao joined to fetch block hash
    public static Transaction getTransactionFromEntity(@NonNull TransactionEntity entity, HeaderEntity headerEntity) {
        Transaction tx = new Transaction(
                entity.version, entity.type, entity.nonce,
                entity.from, entity.gasPrice, entity.amount,
                entity.payload, entity.to, entity.signature,
                headerEntity.blockHash, headerEntity.height,
                entity.wasmPayload
        );
        if (tx.type == Transaction.Type.DEPLOY_CONTRACT.ordinal()) {
            tx.contractType = tx.payload[0];
        }
        if (tx.type == Transaction.Type.CALL_CONTRACT.ordinal()) {
            tx.contractType = tx.payload[0];
            tx.methodType = Transaction.getContract(tx.methodType);
        }
        return tx;
    }

    public static TransactionEntity getEntityFromTransaction(@NonNull Transaction tx) {
        TransactionEntity.TransactionEntityBuilder builder = TransactionEntity.builder()
                .amount(tx.amount)
                .from(tx.from)
                .gasPrice(tx.gasPrice)
                .nonce(tx.nonce)
                .signature(tx.signature)
                .to(tx.to)
                .txHash(tx.getHash())
                .type(tx.type)
                .version(tx.version);
        Transaction.Type type = Transaction.Type.values()[tx.type];
        if(type == Transaction.Type.WASM_CALL || type == Transaction.Type.WASM_DEPLOY){
            builder.wasmPayload(tx.payload);
        }else{
            builder.payload(tx.payload);
        }
        return builder.build();
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
