package com.guoba.lisa.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

public class DateTimeHelperTest {
    @Test
    public void testCalcYearDiff() {
        int age8 = DateTimeHelper.calcYearDiff(LocalDate.of(2022, Month.MARCH, 28),
            LocalDate.of(2014, Month.MARCH, 28));
        Assertions.assertEquals(8, age8);

        int age7 = DateTimeHelper.calcYearDiff(LocalDate.of(2022, Month.MARCH, 27),
            LocalDate.of(2014, Month.MARCH, 28));
        Assertions.assertEquals(7, age7);

        int age9 = DateTimeHelper.calcYearDiff(LocalDate.of(2023, Month.MARCH, 29),
            LocalDate.of(2014, Month.MARCH, 28));
        Assertions.assertEquals(9, age9);
    }
}
