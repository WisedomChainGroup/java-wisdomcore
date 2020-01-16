package org.wisdom.entity;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;


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
    public byte[] block_hash;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    public byte[] tx_hash;

    @Column(name = COLUMN_TX_INDEX)
    public int tx_index;

}
