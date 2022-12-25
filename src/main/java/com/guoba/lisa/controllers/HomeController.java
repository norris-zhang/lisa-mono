package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    @RequestMapping(path = {"", "/"})
    public String home(Authentication auth) {
        Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        if (roles.contains("ROLE_STUDENT")) {
            return "redirect:/my";
        } else if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_TEACHER")) {
            return "redirect:/roll";
        }
        // FIXME redirect to an error page.
        return "redirect:/roll";
    }
}
