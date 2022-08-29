package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.LisaClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LisaClassRepository extends JpaRepository<LisaClass, Long> {

    List<LisaClass> findByInstitutionId(Long institutionId);
}
