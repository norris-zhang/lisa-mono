package com.guoba.lisa.web.models;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AddClass {
    private String name;
    private String classDay;
    private LocalTime startTime;
    private LocalTime endTime;
}
