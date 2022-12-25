package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Parent;
import com.guoba.lisa.datamodel.ParentStudent;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.datamodel.Work;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.dtos.StudentWorkVo;
import com.guoba.lisa.dtos.WorkVo;
import com.guoba.lisa.helpers.DateTimeHelper;
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
import java.util.Set;
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

    public StudentWorkVo getStudentWork(Long stuId, Long institutionId) throws IllegalAccessException {
        Student student = studentRepository.getReferenceById(stuId);
        if (!student.getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Student not found.");
        }

        StudentWorkVo vo = new StudentWorkVo();
        vo.setId(stuId);
        vo.setName(student.getFirstName() + " " + student.getLastName());
        vo.setDateOfBirth(student.getDateOfBirth());
        if (student.getDateOfBirth() != null) {
            vo.setAge(DateTimeHelper.calcAge(student.getDateOfBirth()));
        }
        vo.setCredits(student.getCredits());
        vo.setEnrolledOn(student.getEnrolledOn());
        vo.setClasses(student.getClasses().stream().map(LisaClass::getName).collect(Collectors.joining("|")));
        vo.setParentInfo(deriveParentInfo(student.getParents()));

        List<WorkVo> workVos = new ArrayList<>();
        student.getWorks().forEach(w -> {
            WorkVo wvo = new WorkVo();
            wvo.setId(w.getId());
            wvo.setTitle(w.getTitle());
            wvo.setDescription(w.getDescription());
            wvo.setDate(w.getDate());
            wvo.setPicId(w.getPicture().getId());
            wvo.setPath(w.getPicture().getPath());
            wvo.setMimetype(w.getPicture().getMimetype());
            workVos.add(wvo);
        });

        workVos.sort((w1, w2) -> w2.getDate().compareTo(w1.getDate()));
        vo.setWorks(workVos);
        return vo;
    }

    private String deriveParentInfo(Set<ParentStudent> parents) {
        return parents.stream().map(p -> {
            Parent par = p.getParent();
            return par.getFirstName() + " " + par.getLastName() + " (" + par.getContactNumber() + ")";
        }).collect(Collectors.joining("|"));
    }

    public Student getStudentById(Long stuId, Long institutionId) throws IllegalAccessException {
        Student student = studentRepository.getReferenceById(stuId);
        if (!student.getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Student not found.");
        }
        if (!Hibernate.isInitialized(student.getClasses())) {
            Hibernate.initialize(student.getClasses());
        }
        return student;
    }
}
