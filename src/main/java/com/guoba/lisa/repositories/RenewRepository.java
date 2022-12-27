package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Renew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface RenewRepository extends JpaRepository<Renew, Long> {
    @Query("select sum(r.topupAmount) from Renew r where r.student.institution.id=:institutionId and r.date between :startDate and :endDate")
    Integer findSumTopupCreditsBetween(Long institutionId, LocalDate startDate, LocalDate endDate);
}
