package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.Parent;
import com.guoba.lisa.datamodel.Renew;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.dtos.StudentWorkVo;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.web.models.AddStudent;
import com.guoba.lisa.web.models.AddTopup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {this.studentService = studentService;}

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/api/students")
    public @ResponseBody List<Student> classStudents(Long classId, Model model) {
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

        Parent parent = new Parent();
        parent.setInstitution(institution);
        parent.setFirstName(addStudent.getParentFirstName());
        parent.setLastName(addStudent.getParentLastName());
        parent.setContactNumber(addStudent.getContactNumber());

        try {
            studentService.createStudent(student, addStudent.getParentInfo(), parent);

            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("model", addStudent);
            return "students/add";
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(path = "/my")
    public String studentCentre(Authentication auth, Model model) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        try {
            StudentWorkVo studentWork = studentService.getStudentWorkByLoginUserId(authUser.getUserId(), authUser.getInstitutionId());
            model.addAttribute("studentWorkVo", studentWork);
            return "students/my";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/student/{stuId}")
    public String studentDetail(@PathVariable Long stuId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        try {
            StudentWorkVo swVo = studentService.getStudentWork(stuId, authUser.getInstitutionId());
            model.addAttribute("studentWorkVo", swVo);
            return "students/view";
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/classes");
            return "404";
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/student/topup")
    public String topup(Long stuId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        try {
            Student student = studentService.getStudentByIdInitTopupHistory(stuId, authUser.getInstitutionId());
            model.addAttribute("student", student);
            return "students/topup";
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/students");
            return "404";
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/student/topup")
    public String topup(@ModelAttribute AddTopup addTopup, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser) auth.getPrincipal();

        Renew renew = new Renew();
        renew.setDate(addTopup.getTopupDate());
        renew.setInputDate(ZonedDateTime.now());
        renew.setTopupAmount(addTopup.getTopupAmount());

        try {
            studentService.topup(addTopup.getStuId(), authUser.getInstitutionId(), renew);
            return "redirect:/student/topup?stuId=" + addTopup.getStuId();
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/students");
            return "404";
        }
    }
}
