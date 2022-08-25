package com.guoba.lisa.config;

import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.repositories.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsManagerImpl implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsManagerImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] usernameInstitution = username.split("@");
        LisaUser lisaUser = userRepository.findByUsernameAndInstitutionId(
                usernameInstitution[0], /*Long.valueOf(usernameInstitution[1])*/ 1L);
        if (lisaUser == null) {
            throw new UsernameNotFoundException("Unknown username: " + username);
        }

        UserDetails user = User.builder()
                .username(username)
                .password(lisaUser.getPassword())
                .roles(lisaUser.getRole())
                .build();
        AuthUser authUser = new AuthUser(username, lisaUser.getPassword(), user.getAuthorities());
        authUser.setUserId(lisaUser.getId());
        authUser.setInstitutionId(lisaUser.getInstitution().getId());
        authUser.setInstitutionName(lisaUser.getInstitution().getName());

        return authUser;
    }
}
