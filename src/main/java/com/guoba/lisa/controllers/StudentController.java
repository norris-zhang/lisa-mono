package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.web.models.AddStudent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {this.studentService = studentService;}

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/api/students")
    public @ResponseBody List<Student> classStudents(@RequestParam(name = "classId", required = true) Long classId, Model model) {
        return studentService.getClassStudents(classId);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/students")
    public String listStudents(@RequestParam(required = false, defaultValue = "0") Integer page, Authentication auth,
                               Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        List<StudentVo> students = studentService.getInstitutionStudents(authUser.getInstitutionId(), page, 20);
        model.addAttribute("students", students);
        return "students/list";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/students/add")
    public String addStudent(Authentication auth) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();

        return "students/add";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/students/add")
    public String createStudent(@ModelAttribute AddStudent addStudent, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();

        Student student = new Student();
        Institution institution = new Institution();
        institution.setId(authUser.getInstitutionId());
        student.setInstitution(institution);
        student.setFirstName(addStudent.getFirstName());
        student.setLastName(addStudent.getLastName());
        student.setDateOfBirth(addStudent.getDob());
        student.setEnrolledOn(addStudent.getEnrolmentDate());
        student.setCredits(addStudent.getCredits());

        try {
            studentService.createStudent(student);

            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("model", addStudent);
            return "students/add";
        }
    }
}
