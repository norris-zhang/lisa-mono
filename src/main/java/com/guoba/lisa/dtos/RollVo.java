package com.guoba.lisa.dtos;

import com.guoba.lisa.datamodel.LisaClass;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RollVo {
    private List<LisaClass> classList;
    private LisaClass selectedClass;
    private List<LocalDate> dates;
    private List<RollVoItem> items;

    @Data
    public static class RollVoItem {
        private Long studentId;
        private String name;
        private Integer credits;
        private List<Pair<Boolean, Integer>> rollList;
    }
}
