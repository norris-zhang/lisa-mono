package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.dtos.Pair;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.RollRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static java.lang.Boolean.TRUE;

@Service
public class RollService {
    private final RollRepository rollRepository;
    private final LisaClassRepository classRepository;

    public RollService(RollRepository rollRepository, LisaClassRepository classRepository) {
        this.rollRepository = rollRepository;
        this.classRepository = classRepository;
    }

    public RollVo getRollForClass(Long classId) {
        List<LisaClass> allClasses = classRepository.findAll();
        LisaClass currentClass = resolveCurrentClass(allClasses);

        RollVo vo = new RollVo();
        vo.setClassList(allClasses);
        vo.setSelectedClass(currentClass);


        return vo;
    }

    private LisaClass resolveCurrentClass(List<LisaClass> allClasses) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Pacific/Auckland"));
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        LocalTime localTime = localDateTime.toLocalTime();

        return allClasses.stream()
                .filter(c -> dayOfWeek.name().equals(c.getWeekday()))
                .filter(c -> localTime.isAfter(c.getStartTime()) && localTime.isBefore(c.getEndTime()))
                .findFirst()
                .orElse(allClasses.stream()
                        .findFirst()
                        .orElseThrow());
    }
}
