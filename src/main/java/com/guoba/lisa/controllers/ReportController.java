package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.dtos.ReportVo;
import com.guoba.lisa.services.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {this.reportService = reportService;}

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/report")
    public String report(@RequestParam(defaultValue = "1") Integer statPeriod,
                         @RequestParam(required = false) LocalDate startDate,
                         @RequestParam(required = false) LocalDate endDate, Authentication auth, Model model) {
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        ReportVo vo = reportService.report(authUser.getInstitutionId(), statPeriod, startDate, endDate);
        model.addAttribute("vo", vo);
        return "reports/index";
    }
}
