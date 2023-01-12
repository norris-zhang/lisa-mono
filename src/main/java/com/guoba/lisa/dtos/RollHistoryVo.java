package com.guoba.lisa.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RollHistoryVo {
    private String studentName;
    private String className;
    private LocalDate classDate;
    private Boolean isPresent;
    private Integer creditRedeemed;
}
