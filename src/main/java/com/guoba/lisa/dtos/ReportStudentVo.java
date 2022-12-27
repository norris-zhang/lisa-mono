package com.guoba.lisa.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportStudentVo {
    private Long id;
    private String name;
    private Integer credits;
    private LocalDate lastTopup;
    private Integer lastTopupAmount;
    private String parentName;
    private String parentContact;
}
