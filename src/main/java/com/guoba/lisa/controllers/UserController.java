package com.guoba.lisa.controllers;

import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @GetMapping("/login")
    public String login(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            System.out.println(name + " : " + session.getAttribute(name));
        }
        return "login";
    }
}
