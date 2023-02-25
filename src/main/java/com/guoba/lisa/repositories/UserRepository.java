package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.LisaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<LisaUser, Long> {
    LisaUser findByUsernameAndInstitutionId(String username, Long institutionId);

    @Query("select lu.username from LisaUser lu where lu.institution.id=:institutionId and lower(lu.username) like :fuzzyUsername")
    List<String> findByFuzzyUsernameAndInstitutionId(String fuzzyUsername, Long institutionId);
}
