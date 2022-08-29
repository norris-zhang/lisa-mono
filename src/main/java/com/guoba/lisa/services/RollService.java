package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.dtos.RollVo.RollVoItem;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.RollRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class RollService {
    private final RollRepository rollRepository;
    private final LisaClassRepository classRepository;

    public RollService(RollRepository rollRepository, LisaClassRepository classRepository) {
        this.rollRepository = rollRepository;
        this.classRepository = classRepository;
    }

    public RollVo getRollForClass(Long classId, Long institutionId) {
        List<LisaClass> allClasses = classRepository.findByInstitutionId(institutionId);
        LisaClass currentClass = classId == null
                ? resolveCurrentClass(allClasses)
                : allClasses.stream()
                    .filter(c -> classId.equals(c.getId()))
                    .findAny()
                    .orElseThrow();

        RollVo vo = new RollVo();
        vo.setClassList(allClasses);
        vo.setSelectedClass(currentClass);

        List<Roll> rolls = rollRepository.findByClazzId(Sort.by(DESC, "classDate"));
        rolls.forEach(r -> {
            RollVoItem item = new RollVoItem();
            item.setName(r.getStudent().getFirstName() + (isNotBlank(r.getStudent().getLastName())
                    ? (" " + r.getStudent().getLastName())
                    : ""));

        });


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
