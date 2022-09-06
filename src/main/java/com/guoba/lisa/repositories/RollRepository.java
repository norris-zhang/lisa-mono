package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface RollRepository extends JpaRepository<Roll, Long> {
    List<Roll> findByClazzIdAndClassDateGreaterThanEqual(Long clazzId, LocalDate classDate, Sort sort);

    List<Roll> findByStudentIdAndClazzId(Long studentId, Long clazzId, Pageable pageable);
}
