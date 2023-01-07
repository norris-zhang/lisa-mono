package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.StudentMultipleSelectVo;
import com.guoba.lisa.helpers.DateTimeHelper;
import com.guoba.lisa.services.ClassService;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.web.models.AddClass;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ClassController {
    private final ClassService classService;
    private final StudentService studentService;

    public ClassController(ClassService classService, StudentService studentService) {
        this.classService = classService;
        this.studentService = studentService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @RequestMapping(path = "/classes", method = GET)
    public String list(Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        List<LisaClass> classes = classService.getAllActiveClasses(authUser.getInstitutionId());
        model.addAttribute("classes", classes);
        return "classes/list";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/classes/add")
    public String add() {
        return "classes/add";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/classes/add")
    public String createClass(@ModelAttribute AddClass addClass, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        LisaClass lisaClass = new LisaClass();
        Institution institution = new Institution();
        institution.setId(authUser.getInstitutionId());
        lisaClass.setInstitution(institution);
        lisaClass.setName(addClass.getName());
        lisaClass.setWeekday(addClass.getClassDay());
        lisaClass.setStartTime(addClass.getStartTime());
        lisaClass.setEndTime(addClass.getEndTime());
        lisaClass.setSessionCredits(addClass.getSessionCredits());
        try {
            classService.createClass(lisaClass);
            return "redirect:/classes";
        } catch (Exception e) {
            model.addAttribute("model", addClass);
            model.addAttribute("errorMsg", e.getMessage());
            return "classes/add";
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/lclass/students")
    public String classStudents(Long classId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        LisaClass lisaClass = classService.findClassById(classId);

        if (!lisaClass.getInstitution().getId().equals(authUser.getInstitutionId())) {
            model.addAttribute("errorMsg", "Class not found.");
            model.addAttribute("gobackurl", "/classes");
            return "404";
        }

        model.addAttribute("classInfo", deriveClassInfo(lisaClass));
        model.addAttribute("classId", classId);

        List<Student> classStudents = studentService.getClassStudents(classId);
        model.addAttribute("classStudents", classStudents);
        return "classes/students";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/lclass/addstu")
    public String addClassStudent(Long classId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();

        LisaClass lisaClass = classService.findClassById(classId);
        if (!lisaClass.getInstitution().getId().equals(authUser.getInstitutionId())) {
            model.addAttribute("errorMsg", "Class not found.");
            model.addAttribute("gobackurl", "/classes");
            return "404";
        }
        model.addAttribute("classInfo", deriveClassInfo(lisaClass));
        model.addAttribute("classId", classId);

        List<Student> candidateStudents = studentService.findStudentsOutOfClass(classId, authUser.getInstitutionId());
        List<StudentMultipleSelectVo> voList = new ArrayList<>();
        candidateStudents.forEach(s -> voList.add(studentToMultipleSelectVo(s)));
        model.addAttribute("candidateStudents", voList);

        return "classes/add-student";
    }

    private StudentMultipleSelectVo studentToMultipleSelectVo(Student stu) {
        StudentMultipleSelectVo vo = new StudentMultipleSelectVo();
        vo.setId(stu.getId());
        StringBuilder sb = new StringBuilder();
        sb.append(stu.getFirstName()).append(" ").append(stu.getLastName());
        if (stu.getDateOfBirth() != null) {
            int age = DateTimeHelper.calcAge(stu.getDateOfBirth());
            sb.append(". Age: ").append(age);
        }
        if (stu.getClasses().size() > 0) {
            sb.append(". Currently in classes: ")
                .append(stu.getClasses().stream().map(LisaClass::getName).collect(Collectors.joining("|")));
        }
        vo.setDesc(sb.toString());
        return vo;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/lclass/addstu")
    public String createClassStudent(Long classId, Long[] studentIds) {
        classService.addStudents(classId, studentIds);
        return "redirect:/lclass/students?classId=" + classId;
    }

    private String deriveClassInfo(LisaClass lisaClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("Class ").append(lisaClass.getName()).append(" starts at ").append(lisaClass.getStartTime())
            .append(" on each ").append(lisaClass.getWeekday());

        if (lisaClass.getStudents().size() == 0) {
            sb.append(". There is no student in the class.");
        } else if (lisaClass.getStudents().size() == 1) {
            sb.append(". There is 1 student in the class now.");
        } else {
            sb.append(". There are ").append(lisaClass.getStudents().size()).append(" students now.");
        }
        return sb.toString();
    }
}
