package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Roll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RollRepository extends JpaRepository<Roll, Long>, RollRepositoryCustom {
    List<Roll> findByClazzIdAndClassDateGreaterThanEqual(Long clazzId, LocalDate classDate, Sort sort);

    List<Roll> findByStudentIdAndClazzIdAndClassDateGreaterThan(Long studentId, Long clazzId, LocalDate classDate, Sort sort);

    Optional<Roll> findFirstByStudentIdAndClazzIdAndClassDateLessThanOrderByClassDateDesc(Long studentId, Long clazzId, LocalDate classDate);

    List<Roll> findByClassDateBetweenAndClazzInstitutionId(LocalDate startDate, LocalDate endDate, Long institutionId);

//    @Query("from Roll r where (1=:allClasses or r.clazz.id=:classId) and (1=:allStudents or r.student.id=:stuId) and r.student.institution.id=:institutionId and r.clazz.institution.id=:institutionId")
//    Page<Roll> findByClazzIdAndStudentId(Long classId, Long stuId, Long institutionId, int allClasses, int allStudents, PageRequest page);
}
