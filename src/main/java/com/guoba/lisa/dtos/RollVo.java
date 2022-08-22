package com.guoba.lisa.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.TreeMap;

@Data
public class RollVo {
    private String name;
    private TreeMap<LocalDate, Pair<Boolean, Integer>> rollMap;
}
