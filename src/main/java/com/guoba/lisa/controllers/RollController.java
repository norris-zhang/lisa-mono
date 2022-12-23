package com.guoba.lisa.controllers;

import com.guoba.lisa.config.AuthUser;
import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.exceptions.RollException;
import com.guoba.lisa.services.ClassService;
import com.guoba.lisa.services.RollService;
import com.guoba.lisa.services.StudentService;
import com.guoba.lisa.web.models.RollCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class RollController {
    private final Logger logger = LoggerFactory.getLogger(RollController.class);
    private final RollService rollService;
    private final ClassService classService;
    private final StudentService studentService;

    public RollController(RollService rollService, ClassService classService, StudentService studentService) {
        this.rollService = rollService;
        this.classService = classService;
        this.studentService = studentService;
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

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping(path = "/roll/catchup")
    public String rollCatchup(Long classId, Authentication auth, Model model) {
        model.addAttribute("classId", classId);
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        List<LisaClass> allClasses = classService.getAllClassesInitStudentsByClassId(authUser.getInstitutionId(), classId);
        model.addAttribute("allClasses", allClasses);
        Set<Student> classStudents = allClasses.stream().filter(c -> c.getId().equals(classId)).findAny().get().getStudents();
        model.addAttribute("classStudents", classStudents);
        return "roll/catch-up";
    }
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping(path = "/roll/catchup")
    public String rollCatchupCall(@ModelAttribute RollCall rollCall,
                                  RedirectAttributes redirectModel) {

        try {
            rollService.rollCall(rollCall.getStuId(), rollCall.getClassId(), rollCall.getRollDate(),
                "PRESENT".equals(rollCall.getStatus()), rollCall.getIsDeduct());
            return "redirect:/students/history?stuId=" + rollCall.getStuId();
        } catch (RollException e) {
            redirectModel.addFlashAttribute("rollCallModel", rollCall);
            redirectModel.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/roll/catchup?classId=" + rollCall.getClassId();
        }
    }
}
