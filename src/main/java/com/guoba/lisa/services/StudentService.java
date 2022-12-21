package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {this.studentRepository = studentRepository;}

    public List<Student> getClassStudents(Long classId) {
        return studentRepository.findByClassesId(classId);
    }
}
