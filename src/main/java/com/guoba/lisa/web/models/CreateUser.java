package com.guoba.lisa.web.models;

import lombok.Data;

@Data
public class CreateUser {
    private Long stuId;
    private String username;
    private String password;
}
