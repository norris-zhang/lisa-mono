package com.guoba.lisa.web.models;

import lombok.Data;

@Data
public class StudentSearchModel {
    private String keyword;
    private Integer page = 0;
}
