package com.guoba.lisa.config;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
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
