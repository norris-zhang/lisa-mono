package com.guoba.lisa.helpers;

import com.guoba.lisa.config.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    public static Long institutionId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser)auth.getPrincipal();
        return authUser.getInstitutionId();
    }
}
