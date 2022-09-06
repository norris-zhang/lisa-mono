package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
