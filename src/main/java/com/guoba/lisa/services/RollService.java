package com.guoba.lisa.services;

import com.guoba.lisa.dtos.Pair;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.repositories.RollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static java.lang.Boolean.TRUE;

@Service
public class RollService {
    private final RollRepository rollRepository;

    public RollService(RollRepository rollRepository) {
        this.rollRepository = rollRepository;
    }

    public List<RollVo> getRollForClass(Long classId) {
        List<RollVo> rollVoList = new ArrayList<>();
        RollVo vo = new RollVo();
        Pair<Boolean, Integer> pair = new Pair<>(TRUE, 8);
        vo.setName("Dongchen");
        TreeMap<LocalDate, Pair<Boolean, Integer>> rollMap = new TreeMap<>();
        rollMap.put(LocalDate.of(2022, Month.AUGUST, 27), pair);
        vo.setRollMap(rollMap);
        rollVoList.add(vo);
        return rollVoList;
    }
}
