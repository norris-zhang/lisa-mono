package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClassesId(Long classId);

    @Query(nativeQuery = true,
        value = "select s.* from student s where not exists (select 1 from student_class sc where sc.class_id=:classId and sc.student_id=s.id)")
    List<Student> findStudentsOutOfClass(Long classId);

    List<Student> findByInstitutionId(Long institutionId, Pageable page);

    Student findByUserId(Long userId);

    @Query("select sum(s.credits) from Student s where s.institution.id=:institutionId")
    Integer findSumCreditsByInstitutionId(Long institutionId);

    List<Student> findByInstitutionIdAndCreditsLessThan(Long institutionId, Integer lowCreditThreshold, Sort sort);
}
