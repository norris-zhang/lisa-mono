package com.guoba.lisa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ClassController {
    @RequestMapping(path = "/classes", method = GET)
    public String list() {
        return "classes/list";
    }
}
