package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.Pair;
import com.guoba.lisa.dtos.RollCallVo;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.dtos.RollVo.RollVoItem;
import com.guoba.lisa.exceptions.RollException;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.RollRepository;
import com.guoba.lisa.repositories.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;
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
            voItem.setCredits(stu.getCredits());

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
    public RollCallVo rollCall(Long stuId,
                               Long classId,
                               LocalDate rollDate,
                               boolean isPresent,
                               Boolean isDeduct) throws RollException {
        LisaClass clazz = classRepository.getReferenceById(classId);
        String weekday = clazz.getWeekday();
        DayOfWeek dayOfWeek = rollDate.getDayOfWeek();
        if (!Objects.equals(weekday, dayOfWeek.toString())) {
            throw new RollException(String.format("The roll date %s does not align with the class week day %s.",
                rollDate.toString(), weekday));
        }

        Student student = studentRepository.getReferenceById(stuId);
        Optional<Roll> previousRoll = rollRepository.findFirstByStudentIdAndClazzIdAndClassDateLessThanOrderByClassDateDesc(stuId, classId, rollDate);

        // The new roll item's credit is determined by its previous roll item, or student's credit if no previous roll.
        Integer rollCreditBal = 0;
        if (previousRoll.isPresent()) {
            rollCreditBal = previousRoll.get().getCreditBalance();
        } else {
            rollCreditBal = student.getCredits();
        }

        // The final credit that is set to the student at last.
        Integer newCreditBalance = student.getCredits();
        if (isPresent || (TRUE.equals(isDeduct))) {
            newCreditBalance--;
            rollCreditBal--;
        }

        Roll roll = createRoll(classId, rollDate, isPresent, student, rollCreditBal);

        updateStudentCredits(student, newCreditBalance);

        if (isPresent || (TRUE.equals(isDeduct))) {
            updateSubsequentRolls(stuId, classId, rollDate, newCreditBalance);
        }
        return RollCallVo.builder().roll(roll).stuId(student.getId()).credits(student.getCredits()).build();
    }

    private void updateStudentCredits(Student student, Integer newCreditBalance) {
        student.setCredits(newCreditBalance);
        studentRepository.save(student);
    }

    private Roll createRoll(Long classId, LocalDate rollDate, boolean isPresent, Student student, Integer rollCreditBal) {
        Roll roll = new Roll();
        roll.setStudent(student);
        roll.setClazz(classRepository.getReferenceById(classId));
        roll.setClassDate(rollDate);
        roll.setIsPresent(isPresent ? "Y" : "N");
        roll.setInputDate(ZonedDateTime.now());
        roll.setCreditBalance(rollCreditBal);
        rollRepository.save(roll);
        return roll;
    }

    private void updateSubsequentRolls(Long stuId, Long classId, LocalDate rollDate, Integer newCreditBalance) throws RollException {
        List<Roll> subsequentRolls = rollRepository.findByStudentIdAndClazzIdAndClassDateGreaterThan(stuId, classId, rollDate,
            Sort.by(ASC, "classDate"));
        for (Roll r : subsequentRolls) {
            r.setCreditBalance(r.getCreditBalance() - 1);
            rollRepository.save(r);
        }
        if (!subsequentRolls.isEmpty()) {
            Roll lastRollEntity = subsequentRolls.get(subsequentRolls.size() - 1);
            if (lastRollEntity.getCreditBalance() != newCreditBalance) {
                throw new RollException("Found discrepancy between student credits and roll credits. Please contact administer.");
            }
        }
    }
}
