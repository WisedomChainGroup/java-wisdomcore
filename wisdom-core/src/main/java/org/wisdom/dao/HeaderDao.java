package org.wisdom.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.entity.HeaderEntity;

import java.util.List;
import java.util.Optional;

public interface HeaderDao extends JpaRepository<HeaderEntity, byte[]> {

    Optional<HeaderEntity> findByHeight(long height);

    Optional<HeaderEntity> findTopByOrderByHeightDesc();

    boolean existsByBlockHash(byte[] blockHash);

    HeaderEntity findByBlockHash(byte[] blockHash);

    List<HeaderEntity> findByHeightBetweenOrderByHeight(long start, long end);

    List<HeaderEntity> findByHeightBetweenOrderByHeight(long start, long end, Pageable pageable);

    long countByCreatedAtAfter(long createdAt);
}
