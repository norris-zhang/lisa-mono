package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.Parent;
import com.guoba.lisa.datamodel.ParentStudent;
import com.guoba.lisa.datamodel.Renew;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentVo;
import com.guoba.lisa.dtos.StudentWorkVo;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.web.models.AddStudent;
import com.guoba.lisa.web.models.AddTopup;
import com.guoba.lisa.web.models.StudentSearchModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class StudentController {
    @Value("${page.size.default}")
    private Integer pageSize;
    private final StudentService studentService;

    public StudentController(StudentService studentService) {this.studentService = studentService;}

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/api/students")
    public @ResponseBody List<Student> classStudents(Long classId, Model model) {
        return studentService.getClassStudents(classId);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/students")
    public String listStudents(@ModelAttribute StudentSearchModel searchModel, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        Page<StudentVo> students = studentService.getInstitutionStudents(authUser.getInstitutionId(),
            searchModel.getPage(), pageSize, searchModel.getKeyword());
        model.addAttribute("students", students);
        model.addAttribute("searchModel", searchModel);
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

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/student/{stuId}/update")
    public String studentUpdate(@PathVariable Long stuId, Authentication auth, Model model) throws IllegalAccessException {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        Student student = studentService.getStudentById(stuId, authUser.getInstitutionId());
        AddStudent stuModel = new AddStudent();
        stuModel.setFirstName(student.getFirstName());
        stuModel.setLastName(student.getLastName());
        stuModel.setDob(student.getDateOfBirth());
        stuModel.setEnrolmentDate(student.getEnrolledOn());
        stuModel.setCredits(student.getCredits());
        Set<ParentStudent> parents = student.getParents();
        stuModel.setParentInfo(parents != null && parents.size() > 0);
        if (parents != null && parents.size() > 0) {
            Parent p = parents.stream().findFirst().map(ParentStudent::getParent).get();
            stuModel.setParentFirstName(p.getFirstName());
            stuModel.setParentLastName(p.getLastName());
            stuModel.setContactNumber(p.getContactNumber());
            model.addAttribute("parentId", p.getId());
        }

        model.addAttribute("model", stuModel);
        model.addAttribute("stuId", stuId);
        return "students/update";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/student/{stuId}/update")
    public String doStudentUpdate(@PathVariable Long stuId, @ModelAttribute AddStudent addStudent, Authentication auth, Model model)
        throws IllegalAccessException {

        AuthUser authUser = (AuthUser) auth.getPrincipal();
        Student student = studentService.getStudentById(stuId, authUser.getInstitutionId());

        student.setFirstName(addStudent.getFirstName());
        student.setLastName(addStudent.getLastName());
        student.setDateOfBirth(addStudent.getDob());
        student.setEnrolledOn(addStudent.getEnrolmentDate());
        student.setCredits(addStudent.getCredits());


        Parent parentUpdate = new Parent();
        parentUpdate.setId(addStudent.getParentId());
        parentUpdate.setFirstName(addStudent.getParentFirstName());
        parentUpdate.setLastName(addStudent.getParentLastName());
        parentUpdate.setContactNumber(addStudent.getContactNumber());

        studentService.updateStudent(student, addStudent.getParentInfo(), parentUpdate);

        return "redirect:/students";
    }
}
