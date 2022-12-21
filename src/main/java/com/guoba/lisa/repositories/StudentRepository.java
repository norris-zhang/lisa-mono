package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClassesId(Long classId);
}
