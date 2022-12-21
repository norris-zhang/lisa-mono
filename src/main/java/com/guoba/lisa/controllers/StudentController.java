package com.guoba.lisa.controllers;

import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.services.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {this.studentService = studentService;}

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/api/students")
    public @ResponseBody List<Student> listClasses(@RequestParam(name = "classId", required = true) Long classId, Model model) {
        return studentService.getClassStudents(classId);
    }
}
