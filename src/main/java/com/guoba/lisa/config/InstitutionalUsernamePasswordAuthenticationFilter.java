package com.guoba.lisa.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class InstitutionalUsernamePasswordAuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new MockRequest(request), response);
    }
    static class MockRequest extends HttpServletRequestWrapper {
        public MockRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }
        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);
            if (SPRING_SECURITY_FORM_USERNAME_KEY.equals(paramName)) {
                return value + "@" + super.getParameter("institutionId");
            } else {
                return value;
            }
        }
    }
}
