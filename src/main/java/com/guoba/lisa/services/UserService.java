package com.guoba.lisa.services;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<LisaUser> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        LisaUser dbUser = userRepository.getReferenceById(authUser.getUserId());
        if (!passwordEncoder.matches(oldPassword, dbUser.getPassword())) {
            throw new IllegalArgumentException("Old Password is wrong.");
        }

        dbUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(dbUser);
    }
}
