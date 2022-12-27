package com.guoba.lisa.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReportVo {
    private Integer activeClasses;
    private Integer activeStudents;
    private Integer owingCredits;
    private Integer statPeriod;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer givenSessions;
    private Integer topupCredits;
    private Integer redeemedCredits;
    private BigDecimal avgCreditsPerSession;
    private List<ReportStudentVo> lowCreditStudents;
}
