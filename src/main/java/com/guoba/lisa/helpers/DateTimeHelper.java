package com.guoba.lisa.helpers;

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
}
