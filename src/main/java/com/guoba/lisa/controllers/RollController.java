package com.guoba.lisa.controllers;

import com.guoba.lisa.services.RollService;
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

    @RequestMapping(path = "/roll", method = GET)
    public String listClasses(@RequestParam(name = "classId", required = false) Long classId) {
        rollService.getRollForClass(classId);
        return "roll/index";
    }
}
