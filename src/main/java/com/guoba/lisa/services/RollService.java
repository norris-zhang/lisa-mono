package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.Pair;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.dtos.RollVo.RollVoItem;
import com.guoba.lisa.exceptions.RollException;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.RollRepository;
import com.guoba.lisa.repositories.StudentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
public class RollService {
    private final RollRepository rollRepository;
    private final LisaClassRepository classRepository;
    private final StudentRepository studentRepository;

    public RollService(RollRepository rollRepository,
                       LisaClassRepository classRepository,
                       StudentRepository studentRepository) {
        this.rollRepository = rollRepository;
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
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
        if (currentClass == null) {
            return vo;
        }

        String weekday = currentClass.getWeekday();
        LocalDate immediateDate = LocalDate.now();
        while (!immediateDate.getDayOfWeek().name().equals(weekday)) {
            immediateDate = immediateDate.minusDays(1L);
        }
        LocalDate start = immediateDate.minusWeeks(5L);

        List<Roll> rolls = rollRepository.findByClazzIdAndClassDateGreaterThanEqual(
                currentClass.getId(), start, Sort.by(DESC, "classDate"));
        Map<String, Roll> map = new HashMap<>();
        rolls.forEach(r -> map.put(r.getStudent().getId() + ":" + r.getClassDate(), r));

        vo.setItems(new ArrayList<>());
        vo.setDates(new ArrayList<>());
        for (int i = 0; i < 10; i++) {
            vo.getDates().add(start.plusWeeks(i));
        }
        for (Student stu : currentClass.getStudents()) {
            RollVoItem voItem = new RollVoItem();
            voItem.setStudentId(stu.getId());
            voItem.setName(stu.getFirstName() + (isBlank(stu.getLastName()) ? "" : " " + stu.getLastName()));

            List<Pair<Boolean, Integer>> list = new ArrayList<>();
            for (LocalDate classDate : vo.getDates()) {
                Roll roll = map.get(stu.getId() + ":" + classDate);
                if (roll == null) {
                    list.add(null);
                } else {
                    list.add(new Pair<>("Y".equals(roll.getIsPresent()), roll.getCreditBalance()));
                }
            }
            voItem.setRollList(list);
            vo.getItems().add(voItem);
        }

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
                        .orElse(null));
    }

    @Transactional(readOnly = false)
    public Roll rollCall(Long stuId,
                         Long classId,
                         LocalDate rollDate,
                         boolean isPresent,
                         Boolean isDeduct) throws RollException {
        Student student = studentRepository.getReferenceById(stuId);

        List<Roll> lastRoll = rollRepository.findByStudentIdAndClazzId(stuId, classId,
                PageRequest.of(0, 1, DESC, "classDate"));
        if (!lastRoll.isEmpty()) {
            Roll lastRollEntity = lastRoll.stream().findFirst().get();
            if (lastRollEntity.getClassDate().plusWeeks(1L).equals(rollDate)) {
                if (lastRollEntity.getCreditBalance() != student.getCredits()) {
                    throw new RollException("Found discrepancy between student credits and roll credits. Please contact administer.");
                }
            } else {
                throw new RollException("Found gap between current roll date and last roll date.");
            }
        }

        Integer newCreditBalance = student.getCredits();
        if (isPresent || (TRUE.equals(isDeduct))) {
            newCreditBalance--;
        }

        Roll roll = new Roll();
        roll.setStudent(student);
        roll.setClazz(classRepository.getReferenceById(classId));
        roll.setClassDate(rollDate);
        roll.setIsPresent(isPresent ? "Y" : "N");
        roll.setInputDate(ZonedDateTime.now());
        roll.setCreditBalance(newCreditBalance);
        rollRepository.save(roll);
        student.setCredits(newCreditBalance);
        studentRepository.save(student);
        return roll;
    }
}
