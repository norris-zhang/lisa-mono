package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.datamodel.Parent;
import com.guoba.lisa.datamodel.ParentStudent;
import com.guoba.lisa.datamodel.Renew;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.dtos.StudentWorkVo;
import com.guoba.lisa.dtos.WorkVo;
import com.guoba.lisa.helpers.DateTimeHelper;
import com.guoba.lisa.repositories.ParentRepository;
import com.guoba.lisa.repositories.ParentStudentRepository;
import com.guoba.lisa.repositories.RenewRepository;
import com.guoba.lisa.repositories.StudentRepository;
import com.guoba.lisa.repositories.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final ParentStudentRepository psRepository;
    private final RenewRepository renewRepository;
    private final UserRepository userRepository;

    public StudentService(StudentRepository studentRepository, ParentRepository parentRepository,
                          ParentStudentRepository psRepository, RenewRepository renewRepository,
                          UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.psRepository = psRepository;
        this.renewRepository = renewRepository;
        this.userRepository = userRepository;
    }

    public List<Student> getClassStudents(Long classId) {
        return studentRepository.findByClassesId(classId);
    }

    public Page<StudentVo> getInstitutionStudents(Long institutionId, int pageNumber, int pageSize, String keyword) {
        Page<Student> students;
        if (isBlank(keyword)) {
            students = studentRepository.findByInstitutionId(institutionId,
                PageRequest.of(pageNumber, pageSize, Direction.ASC, "firstName"));
        } else {
            String[] names = keyword.trim().split("\\s+");
            if (names.length == 1) {
                students = studentRepository.findByInstitutionIdAndNameLikeKeyword(institutionId, "%" + names[0] + "%",
                    "%" + names[0] + "%", PageRequest.of(pageNumber, pageSize, Direction.ASC, "firstName"));
            } else {
                students = studentRepository.findByInstitutionIdAndNameLikeKeyword2(institutionId, "%" + names[0] + "%",
                    "%" + names[1] + "%", PageRequest.of(pageNumber, pageSize, Direction.ASC, "firstName"));
            }
        }

        return students.map(s -> {
            StudentVo vo = new StudentVo();
            vo.setId(s.getId());
            vo.setName(s.getFirstName() + " " + s.getLastName());
            vo.setDateOfBirth(s.getDateOfBirth());
            vo.setCredits(s.getCredits());
            if (!Hibernate.isInitialized(s.getClasses())) {
                Hibernate.initialize(s.getClasses());
            }
            vo.setClasses(s.getClasses().stream().map(LisaClass::getName).collect(Collectors.joining(" | ")));
            vo.setEnrolledOn(s.getEnrolledOn());
            vo.setUserId(Optional.ofNullable(s.getUser()).map(LisaUser::getId).orElse(null));
            return vo;
        });
    }

    public List<Student> findStudentsOutOfClass(Long classId, Long institutionId) {
        List<Student> students = studentRepository.findStudentsOutOfClass(classId, institutionId);
        students.forEach(s -> {
            if (!Hibernate.isInitialized(s.getClasses())) {
                Hibernate.initialize(s.getClasses());
            }
        });
        return students;
    }

    @Transactional
    public void createStudent(Student student, Boolean parentInfo, Parent parent) {
        studentRepository.save(student);
        if (Boolean.TRUE.equals(parentInfo)) {
            parentRepository.save(parent);
            ParentStudent ps = new ParentStudent();
            ps.setStudent(student);
            ps.setParent(parent);
            psRepository.save(ps);
        }
    }

    public StudentWorkVo getStudentWork(Long stuId, Long institutionId) throws IllegalAccessException {
        Student student = studentRepository.getReferenceById(stuId);
        if (!student.getInstitution().getId().equals(institutionId)) {
            throw new IllegalAccessException("Student not found.");
        }

        StudentWorkVo vo = new StudentWorkVo();
        vo.setId(stuId);
        vo.setName(student.getFirstName() + " " + student.getLastName());
        vo.setDateOfBirth(student.getDateOfBirth());
        if (student.getDateOfBirth() != null) {
            vo.setAge(DateTimeHelper.calcAge(student.getDateOfBirth()));
        }
        vo.setCredits(student.getCredits());
        vo.setEnrolledOn(student.getEnrolledOn());
        vo.setClasses(student.getClasses().stream().map(LisaClass::getName).collect(Collectors.joining("|")));
        vo.setParentInfo(deriveParentInfo(student.getParents()));

        List<WorkVo> workVos = new ArrayList<>();
        student.getWorks().forEach(w -> {
            WorkVo wvo = new WorkVo();
            wvo.setId(w.getId());
            wvo.setTitle(w.getTitle());
            wvo.setDescription(w.getDescription());
            wvo.setDate(w.getDate());
            wvo.setPicId(w.getPicture().getId());
            wvo.setPath(w.getPicture().getPath());
            wvo.setMimetype(w.getPicture().getMimetype());
            workVos.add(wvo);
        });

        workVos.sort((w1, w2) -> w2.getDate().compareTo(w1.getDate()));
        vo.setWorks(workVos);
        return vo;
    }

    private String deriveParentInfo(Set<ParentStudent> parents) {
        return parents.stream().map(p -> {
            Parent par = p.getParent();
            return par.getFirstName() + " " + par.getLastName() + " (" + par.getContactNumber() + ")";
        }).collect(Collectors.joining("|"));
    }

    public Student getStudentById(Long stuId, Long institutionId) throws IllegalAccessException {
        Optional<Student> stu = studentRepository.findById(stuId);
        if (stu.isEmpty()) {
            throw new IllegalAccessException("Student info not found.");
        }
        Student student = stu.get();
        if (!Objects.equals(student.getInstitution().getId(), institutionId)) {
            throw new IllegalAccessException("Student info not found.");
        }

        if (!Hibernate.isInitialized(student.getClasses())) {
            Hibernate.initialize(student.getClasses());
        }
        if (!Hibernate.isInitialized(student.getUser())) {
            Hibernate.initialize(student.getUser());
        }
        return student;
    }

    public StudentWorkVo getStudentWorkByLoginUserId(Long userId, Long institutionId) throws IllegalAccessException {
        Student student = studentRepository.findByUserId(userId);

        return getStudentWork(student.getId(), institutionId);
    }

    public Student getStudentByIdInitTopupHistory(Long stuId, Long institutionId) throws IllegalAccessException {
        Student student = getStudentById(stuId, institutionId);
        if (!Hibernate.isInitialized(student.getRenews())) {
            Hibernate.initialize(student.getRenews());
        }
        return student;
    }

    @Transactional(rollbackFor = IllegalAccessException.class)
    public void topup(Long stuId, Long institutionId, Renew renew) throws IllegalAccessException {
        Student student = getStudentById(stuId, institutionId);
        renew.setStudent(student);
        renew.setOpeningBalance(student.getCredits());
        Integer newBalance = student.getCredits() + renew.getTopupAmount();
        renew.setNewBalance(newBalance);
        renewRepository.save(renew);
        student.setCredits(newBalance);
        studentRepository.save(student);
    }

    public String suggestUsername(Student student, Long institutionId) {
        StringBuilder sb = new StringBuilder(student.getFirstName());
        if (isNotBlank(student.getLastName())) {
            sb.append(student.getLastName().charAt(0));
        }
        String suggestedUsername = sb.toString().toLowerCase();
        List<String> duplicateUsers = userRepository.findByFuzzyUsernameAndInstitutionId(suggestedUsername + "%", institutionId);
        if (duplicateUsers.isEmpty()) {
            return suggestedUsername;
        }
        int maxNum = 1;
        boolean isDup = false;
        for (String dup : duplicateUsers) {
            if (dup.equalsIgnoreCase(suggestedUsername)) {
                isDup = true;
            }
            String num = dup.replace(suggestedUsername, "");
            if (num.matches("\\d+")) {
                int theNum = Integer.parseInt(num);
                if (theNum > maxNum) {
                    maxNum = theNum;
                }
            }
        }
        if (!isDup) {
            return suggestedUsername;
        } else {
            return suggestedUsername + (maxNum + 1);
        }
    }

    @Transactional
    public void updateStudent(Student student, Boolean parentInfo, Parent parentUpdate) {
        parentInfo = parentInfo != null && parentInfo;
        Set<ParentStudent> parents = student.getParents();
        if (parents != null && parents.size() > 0 && !parentInfo) {
            // delete parent
            parents.forEach(ps -> {
                psRepository.delete(ps);
                parentRepository.delete(ps.getParent());
            });
        } else if ((parents == null || parents.size() == 0) && parentInfo) {
            // add new parent
            parentUpdate.setId(null);
            parentUpdate.setInstitution(student.getInstitution());
            parentRepository.save(parentUpdate);
            ParentStudent ps = new ParentStudent();
            ps.setStudent(student);
            ps.setParent(parentUpdate);
            psRepository.save(ps);
        } else if (parents != null && parents.size() > 0 && parentInfo) {
            // update existing parent
            Parent parent = parents.stream().map(ParentStudent::getParent).filter(p -> p.getId().equals(parentUpdate.getId())).findAny().orElseThrow();
            parent.setFirstName(parentUpdate.getFirstName());
            parent.setLastName(parentUpdate.getLastName());
            parent.setContactNumber(parentUpdate.getContactNumber());
            parentRepository.save(parent);
        }
        studentRepository.save(student);
    }
}
