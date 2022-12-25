package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<LisaUser> getAll() {
        return userRepository.findAll();
    }
}
