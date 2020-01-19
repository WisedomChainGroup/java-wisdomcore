package org.wisdom.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wisdom.entity.TransactionEntity;

import java.util.List;

public interface TransactionDao extends JpaRepository<TransactionEntity, byte[]> {

    TransactionEntity findByTxHash(byte[] txHash);

    boolean existsByTxHash(byte[] txHash);

    List<TransactionEntity> findByTo(byte[] to, Pageable pageable);

    boolean existsByTypeAndPayload(int type, byte[] payload);

    List<TransactionEntity> findByToAndType(byte[] to, int type, Pageable pageable);

    List<TransactionEntity> findByFromAndTo(byte[] from, byte[] to, Pageable pageable);

    List<TransactionEntity> findByFromAndToAndType(byte[] from, byte[] to, int type, Pageable pageable);

    List<TransactionEntity> findByFromAndType(byte[] from, int type, Pageable pageable);

    List<TransactionEntity> findByFrom(byte[] from, Pageable pageable);
}
