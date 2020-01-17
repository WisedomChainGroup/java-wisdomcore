package org.wisdom.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = TransactionIndexEntity.TABLE_TRANSACTION_INDEX, indexes = {
        @Index(name = "block_hash_index", columnList = TransactionIndexEntity.COLUMN_BLOCK_HASH),
        @Index(name = "tx_hash_index", columnList = TransactionIndexEntity.COLUMN_TX_HASH),
})
public class TransactionIndexEntity {

    static final String COLUMN_BLOCK_HASH = "block_hash";
    static final String COLUMN_TX_HASH = "tx_hash";
    static final String COLUMN_TX_INDEX = "tx_index";
    static final String TABLE_TRANSACTION_INDEX = "transaction_index";

    @Column(name = COLUMN_BLOCK_HASH, nullable = false)
    @Id
    public byte[] blockHash;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    public byte[] txHash;

    @Column(name = COLUMN_TX_INDEX)
    public int txIndex;

}
