package com.guoba.lisa.enums;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;

import static com.guoba.lisa.helpers.DateTimeHelper.last30Days;
import static com.guoba.lisa.helpers.DateTimeHelper.lastMonth;
import static com.guoba.lisa.helpers.DateTimeHelper.lastYear;
import static com.guoba.lisa.helpers.DateTimeHelper.thisMonth;
import static com.guoba.lisa.helpers.DateTimeHelper.thisYear;

public enum StatPeriod {
    LAST_30_DAYS(1, "the last 30 days"),
    LAST_MONTH(2, "last month"),
    THIS_MONTH(3, "this month"),
    THIS_YEAR(4, "this year"),
    LAST_YEAR(5, "last year"),
    CUSTOM(6, "custom");

    private final Integer pvalue;
    private final String pdisplay;
    StatPeriod(Integer pvalue, String pdisplay) {
        this.pvalue = pvalue;
        this.pdisplay = pdisplay;
    }

    public static StatPeriod fromPvalue(Integer pvalue) {
        switch (pvalue) {
            case 1: return LAST_30_DAYS;
            case 2: return LAST_MONTH;
            case 3: return THIS_MONTH;
            case 4: return THIS_YEAR;
            case 5: return LAST_YEAR;
            default: return CUSTOM;
        }
    }

    public Pair<LocalDate, LocalDate> calcPeriod(LocalDate start, LocalDate end) {
        switch (this.pvalue) {
            case 1: return last30Days();
            case 2: return lastMonth();
            case 3: return thisMonth();
            case 4: return thisYear();
            case 5: return lastYear();
            default: return Pair.of(start == null ? LocalDate.MIN : start, end == null ? LocalDate.MAX : end);
        }
    }
}
