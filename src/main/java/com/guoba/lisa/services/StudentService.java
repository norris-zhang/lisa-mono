package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.repositories.StudentRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {this.studentRepository = studentRepository;}

    public List<Student> getClassStudents(Long classId) {
        return studentRepository.findByClassesId(classId);
    }

    public List<StudentVo> getInstitutionStudents(Long institutionId, int pageNumber, int pageSize) {
        List<Student> students = studentRepository.findByInstitutionId(institutionId, PageRequest.of(pageNumber,
            pageSize, Direction.ASC, "firstName"));
        List<StudentVo> voList = new ArrayList<>();
        students.forEach(s -> {
            StudentVo vo = new StudentVo();
            vo.setName(s.getFirstName() + " " + s.getLastName());
            vo.setDateOfBirth(s.getDateOfBirth());
            vo.setCredits(s.getCredits());
            if (!Hibernate.isInitialized(s.getClasses())) {
                Hibernate.initialize(s.getClasses());
            }
            vo.setClasses(s.getClasses().stream().map(LisaClass::getName).collect(Collectors.joining(" | ")));
            vo.setEnrolledOn(s.getEnrolledOn());
            voList.add(vo);
        });
        return voList;
    }

    public List<Student> findStudentsOutOfClass(Long classId) {
        List<Student> students = studentRepository.findStudentsOutOfClass(classId);
        students.forEach(s -> {
            if (!Hibernate.isInitialized(s.getClasses())) {
                Hibernate.initialize(s.getClasses());
            }
        });
        return students;
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }
}
