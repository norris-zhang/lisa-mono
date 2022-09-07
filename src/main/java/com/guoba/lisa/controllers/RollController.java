package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.exceptions.RollException;
import com.guoba.lisa.services.RollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class RollController {
    private final Logger logger = LoggerFactory.getLogger(RollController.class);
    private final RollService rollService;

    public RollController(RollService rollService) {
        this.rollService = rollService;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @RequestMapping(path = "/roll", method = {GET, POST})
    public String listClasses(@RequestParam(name = "classId", required = false) Long classId, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RollVo rollVo = rollService.getRollForClass(classId, authUser.getInstitutionId());
        model.addAttribute("rollVo", rollVo);
        return "roll/index";
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/roll/call", produces = "application/json")
    public @ResponseBody Map<String, String> rollCall(Long stuId,
                                                      Long classId,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate rollDate,
                                                      String status,
                                                      @RequestParam(required = false) Boolean isDeduct) {
        try {
            Roll roll = rollService.rollCall(stuId, classId, rollDate, "PRESENT".equals(status), isDeduct);
            Map<String, String> map = new HashMap<>();
            map.put("status", "ok");
            map.put("credit", roll.getCreditBalance().toString());
            map.put("isPresent", roll.getIsPresent());
            return map;
        } catch (RollException e) {
            Map<String, String> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", e.getMessage());
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map<String, String> map = new HashMap<>();
            map.put("status", "error");
            map.put("message", "Server Error. Please contact administrator.");
            return map;
        }
    }
}
