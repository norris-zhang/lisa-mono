package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.helpers.WordHelper;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.services.UserService;
import com.guoba.lisa.web.models.ChangePassword;
import com.guoba.lisa.web.models.CreateUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class UserController {
    private final UserService userService;
    private final StudentService studentService;

    public UserController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @RequestMapping(path = "/user/list", method = GET)
    public String list(Model model) {
        List<LisaUser> users = userService.getAll();
        model.addAttribute("users", users);
        return "user/list";
    }

    @RequestMapping("/login")
    public String login(HttpSession session, HttpServletRequest request, Authentication auth, Model model) {
        return "login";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String doChangePassword(ChangePassword form, Model model) {
        try {
            userService.changePassword(form.getOldPassword(), form.getNewPassword());
            return "redirect:/logout";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/");
            return "404";
        }
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/create-user")
    public String createUser(Long stuId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        model.addAttribute("stuId", stuId);
        try {
            Student student = studentService.getStudentById(stuId, authUser.getInstitutionId());
            model.addAttribute("suggestedUsername", studentService.suggestUsername(student, authUser.getInstitutionId()));
            model.addAttribute("suggestedPassword", WordHelper.randomWord());
            model.addAttribute("student", student);
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/");
            return "404";
        }
        return "user/create-user";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/create-user")
    public String doCreateUser(@ModelAttribute CreateUser studentUser, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        try {
            userService.createStudentUser(studentUser, authUser.getInstitutionId());
            return "redirect:/students";
        } catch (IllegalAccessException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/");
            return "404";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("gobackurl", "/students");
            return "500";
        }
    }
}
