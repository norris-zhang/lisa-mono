package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.LisaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<LisaUser, Long> {
    LisaUser findByUsernameAndInstitutionId(String username, Long institutionId);
}
