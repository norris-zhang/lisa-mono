package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Institution;
import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.services.ClassService;
import com.guoba.lisa.web.models.AddClass;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {this.classService = classService;}

    @RequestMapping(path = "/classes", method = GET)
    public String list() {
        return "classes/list";
    }

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
        try {
            classService.createClass(lisaClass);
            return "redirect:/classes";
        } catch (Exception e) {
            model.addAttribute("model", addClass);
            model.addAttribute("errorMsg", e.getMessage());
            return "classes/add";
        }
    }
}
