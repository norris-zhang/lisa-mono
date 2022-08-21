package com.guoba.lisa.controllers;

import com.guoba.lisa.datamodel.LisaUser;
import com.guoba.lisa.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
