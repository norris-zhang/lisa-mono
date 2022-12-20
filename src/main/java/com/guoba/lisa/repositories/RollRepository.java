package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RollRepository extends JpaRepository<Roll, Long> {
    List<Roll> findByClazzIdAndClassDateGreaterThanEqual(Long clazzId, LocalDate classDate, Sort sort);

    List<Roll> findByStudentIdAndClazzIdAndClassDateGreaterThan(Long studentId, Long clazzId, LocalDate classDate, Sort sort);

    Optional<Roll> findFirstByStudentIdAndClazzIdAndClassDateLessThanOrderByClassDateDesc(Long studentId, Long clazzId, LocalDate classDate);
}
