package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.repositories.LisaClassRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClassService {
    private final LisaClassRepository classRepository;

    public ClassService(LisaClassRepository classRepository) {this.classRepository = classRepository;}

    public List<LisaClass> getAllClassesInitStudentsByClassId(Long institutionId, Long initClassId) {
        List<LisaClass> allClasses = classRepository.findByInstitutionId(institutionId);
        LisaClass defaultClass = allClasses.stream().filter(c -> c.getId().equals(initClassId)).findAny().get();
        if (!Hibernate.isInitialized(defaultClass.getStudents())) {
            Hibernate.initialize(defaultClass.getStudents());
        }
        return allClasses;
    }
}
