package com.guoba.lisa.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(IllegalAccessException.class)
    public String handleIllegalAccessException(IllegalAccessException e, Model model) {
        e.printStackTrace();
        model.addAttribute("errorMsg", e.getMessage());
        model.addAttribute("gobackurl", "/");
        return "404";
    }
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        e.printStackTrace();
        model.addAttribute("errorMsg", e.getMessage());
        model.addAttribute("gobackurl", "/");
        return "500";
    }
}
