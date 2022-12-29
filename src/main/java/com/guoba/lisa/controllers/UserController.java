package com.guoba.lisa.controllers;

import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/user/list", method = GET)
    public String list(Model model) {
        List<LisaUser> users = userService.getAll();
        model.addAttribute("users", users);
        return "user/list";
    }

    @RequestMapping("/login")
    public String login(HttpSession session, HttpServletRequest request, Authentication auth, Model model) {
        String institutionValue = request.getHeader("Institution-Value");
        String institutionText = request.getHeader("Institution-Text");
        if (isBlank(institutionValue)) {
            institutionValue = "1";
        }
        if (isBlank(institutionText)) {
            institutionText = "LisaArt";
        }
        model.addAttribute("institutionValue", institutionValue);
        model.addAttribute("institutionText", institutionText);
        return "login";
    }
}
