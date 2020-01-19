package org.wisdom.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.entity.HeaderEntity;

import java.util.List;
import java.util.Optional;

public interface HeaderDao extends JpaRepository<HeaderEntity, byte[]> {

    HeaderEntity findByHeight(long height);

    boolean existsByBlockHash(byte[] blockHash);

    Optional<HeaderEntity> findTopByOrderByHeightDesc();

    HeaderEntity findByBlockHash(byte[] blockHash);

    List<HeaderEntity> findByHeightBetweenOrderByHeight(long start, long end);

    List<HeaderEntity> findByHeightBetweenOrderByHeightAsc(long start, long end, Pageable pageable);

    List<HeaderEntity> findByHeightBetweenOrderByHeightDesc(long start, long end, Pageable pageable);

    List<HeaderEntity> findByCreatedAtAfter(long createdAt);
}
