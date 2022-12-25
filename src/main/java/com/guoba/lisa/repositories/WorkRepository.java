package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
