package com.guoba.lisa.dtos;

import lombok.Data;

import java.util.List;

@Data
public class StudentWorkVo extends StudentVo {
    private Long id;
    private Integer age;
    private String parentInfo;
    private List<WorkVo> works;
}
