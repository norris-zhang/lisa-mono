package com.guoba.lisa.web.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddWork {
    private Long stuId;
    private Long workClass;
    private LocalDate workDate;
    private String workTitle;
    private String workDesc;

}
