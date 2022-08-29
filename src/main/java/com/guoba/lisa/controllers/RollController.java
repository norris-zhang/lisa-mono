package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.services.RollService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RollController {
    private final RollService rollService;

    public RollController(RollService rollService) {
        this.rollService = rollService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @RequestMapping(path = "/roll", method = GET)
    public String listClasses(@RequestParam(name = "classId", required = false) Long classId, Authentication auth) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        rollService.getRollForClass(classId, authUser.getInstitutionId());
        return "roll/index";
    }
}
