package com.guoba.lisa.web.models;

import lombok.Data;

@Data
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
