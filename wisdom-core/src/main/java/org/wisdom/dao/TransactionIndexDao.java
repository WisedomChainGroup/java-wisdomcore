package org.wisdom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.entity.TransactionIndexEntity;

import java.util.List;

public interface TransactionIndexDao extends JpaRepository<TransactionIndexEntity, byte[]> {

    List<TransactionIndexEntity> findTransactionIndexEntityByBlockHash(byte[] block_hash);

}
