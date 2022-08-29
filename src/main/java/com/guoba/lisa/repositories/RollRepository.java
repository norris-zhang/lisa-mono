package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RollRepository extends JpaRepository<Roll, Long> {
    List<Roll> findByClazzId(Sort sort);
}
