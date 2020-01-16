package org.wisdom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.entity.TransactionEntity;

public interface TransactionDao extends JpaRepository<TransactionEntity, byte[]> {
}
