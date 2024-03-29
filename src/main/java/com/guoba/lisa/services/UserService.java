package com.guoba.lisa.services;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.repositories.StudentRepository;
import com.guoba.lisa.repositories.UserRepository;
import com.guoba.lisa.web.models.CreateUser;
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
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, StudentRepository studentRepository,
                       StudentService studentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
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

    @Transactional(rollbackFor = Exception.class)
    public void createStudentUser(CreateUser studentUser, Long institutionId) throws Exception {
        Student student = studentService.getStudentById(studentUser.getStuId(), institutionId);

        LisaUser existingUser = userRepository.findByUsernameAndInstitutionId(studentUser.getUsername(), institutionId);
        if (existingUser != null) {
            throw new Exception("Username exists.");
        }

        LisaUser user = new LisaUser();
        user.setUsername(studentUser.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(studentUser.getPassword()));
        user.setRole("STUDENT");
        user.setInstitution(student.getInstitution());
        userRepository.save(user);

        student.setUser(user);
        studentRepository.save(student);

    }

    @Transactional(rollbackFor = Exception.class)
    public void resetStudentPassword(CreateUser studentUser, Long institutionId) throws Exception {
        Student student = studentService.getStudentById(studentUser.getStuId(), institutionId);

        LisaUser user = userRepository.getReferenceById(student.getUser().getId());

        user.setPassword(passwordEncoder.encode(studentUser.getPassword()));
        userRepository.save(user);

    }
}
