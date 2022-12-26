package com.guoba.lisa.web.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddTopup {
    private Long stuId;
    private LocalDate topupDate;
    private Integer topupAmount;
}
