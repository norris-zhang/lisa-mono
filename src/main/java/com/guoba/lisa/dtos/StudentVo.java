package com.guoba.lisa.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentVo {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Integer credits;
    private String classes;
    private LocalDate enrolledOn;
}
