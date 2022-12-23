package com.guoba.lisa.web.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RollCall {
    private Long stuId;
    private Long classId;
    private LocalDate rollDate;
    private String status;
    private Boolean isDeduct;
}
