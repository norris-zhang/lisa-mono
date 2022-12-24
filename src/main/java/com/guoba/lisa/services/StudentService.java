package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Parent;
import com.guoba.lisa.datamodel.ParentStudent;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.repositories.ParentRepository;
import com.guoba.lisa.repositories.ParentStudentRepository;
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
    private final ParentRepository parentRepository;
    private final ParentStudentRepository psRepository;

    public StudentService(StudentRepository studentRepository, ParentRepository parentRepository,
                          ParentStudentRepository psRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.psRepository = psRepository;
    }

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
    public void createStudent(Student student, Boolean parentInfo, Parent parent) {
        studentRepository.save(student);
        if (Boolean.TRUE.equals(parentInfo)) {
            parentRepository.save(parent);
            ParentStudent ps = new ParentStudent();
            ps.setStudent(student);
            ps.setParent(parent);
            psRepository.save(ps);
        }
    }
}
