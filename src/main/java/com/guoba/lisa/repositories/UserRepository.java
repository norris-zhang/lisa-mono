package com.guoba.lisa.repositories;

import com.guoba.lisa.datamodel.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
