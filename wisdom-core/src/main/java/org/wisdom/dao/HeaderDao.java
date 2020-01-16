package org.wisdom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wisdom.entity.HeaderEntity;

public interface HeaderDao extends JpaRepository<HeaderEntity, byte[]> {

    HeaderEntity findByHeight(long height);

}
