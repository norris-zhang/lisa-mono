package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.enums.ClassStatus;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.StudentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClassService {
    private final LisaClassRepository classRepository;
    private final StudentRepository studentRepository;

    public ClassService(LisaClassRepository classRepository, StudentRepository studentRepository) {
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
    }

    public List<LisaClass> getAllClassesInitStudentsByClassId(Long institutionId, Long initClassId) {
        List<LisaClass> allClasses = classRepository.findByInstitutionId(institutionId);
        LisaClass defaultClass = allClasses.stream().filter(c -> c.getId().equals(initClassId)).findAny().get();
        if (!Hibernate.isInitialized(defaultClass.getStudents())) {
            Hibernate.initialize(defaultClass.getStudents());
        }
        return allClasses;
    }

    @Transactional
    public void createClass(LisaClass lisaClass) {
        classRepository.save(lisaClass);
    }

    public List<LisaClass> getAllActiveClasses(Long institutionId) {
        return classRepository.findByInstitutionIdAndStatus(institutionId, ClassStatus.ACTIVE.ordinal());
    }

    public LisaClass findClassById(Long classId) {
        LisaClass lisaClass = classRepository.findById(classId).orElseThrow();
        if (!Hibernate.isInitialized(lisaClass.getStudents())) {
            Hibernate.initialize(lisaClass.getStudents());
        }
        return lisaClass;
    }

    @Transactional
    public void addStudents(Long classId, Long[] studentIds) {
        LisaClass lisaClass = classRepository.getReferenceById(classId);
        List<Student> students = studentRepository.findAllById(Arrays.stream(studentIds).toList());
        lisaClass.getStudents().addAll(students);
        classRepository.save(lisaClass);
    }
}
