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
@Table(name = HeaderEntity.TABLE_HEADER, indexes = {
        @Index(name = "hash_prev_index", columnList = HeaderEntity.COLUMN_HASH_PREV),
        @Index(name = "hash_height_index", columnList = HeaderEntity.COLUMN_HEIGHT),
        @Index(name = "created_at_index", columnList = HeaderEntity.COLUMN_CREATED_AT),
        @Index(name = "block_hash_index", columnList = HeaderEntity.COLUMN_HASH),
})
public class HeaderEntity {

    static final String COLUMN_HASH = "block_hash";
    static final String COLUMN_VERSION = "version";
    static final String COLUMN_HASH_PREV = "hash_prev_block";
    static final String COLUMN_MERKLE_ROOT = "hash_merkle_root";
    static final String COLUMN_MERKLE_STATE = "hash_merkle_state";
    static final String COLUMN_MERKLE_INCUBATE = "hash_merkle_incubate";
    static final String COLUMN_HEIGHT = "height";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_BITS = "nbits";
    static final String COLUMN_NONCE = "nonce";
    static final String COLUMN_BLOCK_NOTICE = "block_notice";
    static final String COLUMN_TOTAL_WEIGHT = "total_weight";
    static final String COLUMN_IS_CANONICAL = "is_canonical";
    static final String TABLE_HEADER = "header";

    @Column(name = COLUMN_HASH, nullable = false)
    @Id
    public byte[] blockHash;

    @Column(name = COLUMN_VERSION, nullable = false)
    public long version;

    @Column(name = COLUMN_HASH_PREV, nullable = false)
    public byte[] hashPrevBlock;

    @Column(name = COLUMN_MERKLE_ROOT, nullable = false)
    public byte[] hashMerkleRoot;

    @Column(name = COLUMN_MERKLE_STATE, nullable = false)
    public byte[] hashMerkleState;

    @Column(name = COLUMN_MERKLE_INCUBATE, nullable = false)
    public byte[] hashMerkleIncubate;

    @Column(name = COLUMN_HEIGHT, nullable = false)
    public long height;

    @Column(name = COLUMN_CREATED_AT, nullable = false)
    public long createdAt;

    @Column(name = COLUMN_BITS, nullable = false)
    public byte[] nBits;

    @Column(name = COLUMN_NONCE, nullable = false)
    public byte[] nNonce;

    @Column(name = COLUMN_BLOCK_NOTICE, nullable = false)
    public byte[] blockNotice;

    @Column(name = COLUMN_TOTAL_WEIGHT, nullable = false)
    public long totalWeight;

    @Column(name = COLUMN_IS_CANONICAL, nullable = false)
    public boolean is_canonical;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "transaction_index", joinColumns = {@JoinColumn(name = "block_hash")}
//            , inverseJoinColumns = {@JoinColumn(name = "tx_hash")})
//    public List<TransactionEntity> transactions;

}
