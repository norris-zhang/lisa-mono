package com.guoba.lisa.controllers.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class InstitutionHeaders implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) {
            return;
        }
        String institutionValue = request.getHeader("Institution-Value");
        String institutionText = request.getHeader("Institution-Text");
        if (isBlank(institutionValue)) {
            institutionValue = "1";
        }
        if (isBlank(institutionText)) {
            institutionText = "LisaArt";
        }
        modelAndView.addObject("institutionValue", institutionValue);
        modelAndView.addObject("institutionText", institutionText);
    }
}
