package com.guoba.lisa.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentWorkVo extends StudentVo {
    private Integer age;
    private String parentInfo;
    private List<WorkVo> works;
}
