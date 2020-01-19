package org.wisdom.entity;


import lombok.*;
import org.tdf.common.util.FastByteComparisons;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = TransactionIndexEntity.TABLE_TRANSACTION_INDEX, indexes = {
        @Index(name = "block_hash_index", columnList = TransactionIndexEntity.COLUMN_BLOCK_HASH),
        @Index(name = "tx_hash_index", columnList = TransactionIndexEntity.COLUMN_TX_HASH),
})
@IdClass(TransactionIndexEntity.TransactionIndexID.class)
public class TransactionIndexEntity {

    static final String COLUMN_BLOCK_HASH = "block_hash";
    static final String COLUMN_TX_HASH = "tx_hash";
    static final String COLUMN_TX_INDEX = "tx_index";
    static final String TABLE_TRANSACTION_INDEX = "transaction_index";

    @Column(name = COLUMN_BLOCK_HASH, nullable = false)
    @Id
    public byte[] blockHash;

    @Column(name = COLUMN_TX_HASH, nullable = false)
    @Id
    public byte[] txHash;

    @Column(name = COLUMN_TX_INDEX)
    public int txIndex;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public final static class TransactionIndexID implements Serializable {
        private byte[] blockHash;
        private byte[] txHash;


        @Override
        public boolean equals(Object o) {
            if(!(o instanceof TransactionIndexID)) return false;
            return FastByteComparisons.equal(blockHash, ((TransactionIndexID) o).blockHash) &&
                    FastByteComparisons.equal(txHash, ((TransactionIndexID) o).txHash);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(blockHash);
            result = 31 * result + Arrays.hashCode(txHash);
            return result;
        }
    }
}
