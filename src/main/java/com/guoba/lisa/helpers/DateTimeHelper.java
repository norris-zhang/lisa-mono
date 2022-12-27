package com.guoba.lisa.helpers;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateTimeHelper {
    /**
     * returns the age based on the date of birth.
     * @param dob
     * @return
     */
    public static int calcAge(LocalDate dob) {
        return calcYearDiff(LocalDate.now(), dob);
    }

    /**
     * returns first - second in years.
     * @param first
     * @param second
     * @return
     */
    public static int calcYearDiff(LocalDate first, LocalDate second) {
        return (int)second.until(first, ChronoUnit.YEARS);
    }

    /**
     * Including today
     * @return
     */
    public static Pair<LocalDate, LocalDate> last30Days() {
        LocalDate end = LocalDate.now().plusDays(1L);
        LocalDate start = end.minusDays(30L);
        return Pair.of(start, end);
    }

    public static Pair<LocalDate, LocalDate> lastMonth() {
        LocalDate start = LocalDate.now().minusMonths(1L);
        start = start.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1L);
        return Pair.of(start, end);
    }

    public static Pair<LocalDate, LocalDate> thisMonth() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1);
        end = end.plusDays(1L);
        return Pair.of(start, end);
    }

    public static Pair<LocalDate, LocalDate> thisYear() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfYear(1);
        end = end.plusDays(1L);
        return Pair.of(start, end);
    }

    public static Pair<LocalDate, LocalDate> lastYear() {
        LocalDate start = LocalDate.now().minusYears(1L);
        start = start.withDayOfYear(1);
        LocalDate end = start.plusYears(1L);
        return Pair.of(start, end);
    }
}
