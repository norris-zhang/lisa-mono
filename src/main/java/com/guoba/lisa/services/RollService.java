package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.LisaClass;
import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.RollCallVo;
import com.guoba.lisa.dtos.RollHistoryVo;
import com.guoba.lisa.dtos.RollVo;
import com.guoba.lisa.dtos.RollVo.RollVoItem;
import com.guoba.lisa.exceptions.RollException;
import com.guoba.lisa.repositories.LisaClassRepository;
import com.guoba.lisa.repositories.RollRepository;
import com.guoba.lisa.repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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

import static com.guoba.lisa.helpers.AuthHelper.institutionId;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Order.desc;

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

            List<Boolean> list = new ArrayList<>();
            for (LocalDate classDate : vo.getDates()) {
                Roll roll = map.get(stu.getId() + ":" + classDate);
                if (roll == null) {
                    list.add(null);
                } else {
                    list.add("Y".equals(roll.getIsPresent()));
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

    @Transactional(rollbackFor = RollException.class)
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
                rollDate, weekday));
        }

        Student student = studentRepository.getReferenceById(stuId);

        // The final credit that is set to the student at last.
        Integer newCreditBalance = student.getCredits();
        Integer creditRedeemed = 0;
        if (isPresent || (TRUE.equals(isDeduct))) {
            Integer sessionCredits = clazz.getSessionCredits();
            newCreditBalance -= sessionCredits;
            creditRedeemed = sessionCredits;
        }

        Roll roll = createRoll(classId, rollDate, isPresent, student, creditRedeemed);

        updateStudentCredits(student, newCreditBalance);

        return RollCallVo.builder().roll(roll).stuId(student.getId()).credits(student.getCredits()).build();
    }

    private void updateStudentCredits(Student student, Integer newCreditBalance) {
        student.setCredits(newCreditBalance);
        studentRepository.save(student);
    }

    private Roll createRoll(Long classId, LocalDate rollDate, boolean isPresent, Student student, Integer creditRedeemed) {
        Roll roll = new Roll();
        roll.setStudent(student);
        roll.setClazz(classRepository.getReferenceById(classId));
        roll.setClassDate(rollDate);
        roll.setIsPresent(isPresent ? "Y" : "N");
        roll.setInputDate(ZonedDateTime.now());
        roll.setCreditRedeemed(creditRedeemed);
        rollRepository.save(roll);
        return roll;
    }

    public Page<RollHistoryVo> getRollForClassAndStudent(String classKeyword, String stuKeyword, int pageNumber, int pageSize) {
        Page<Roll> rollList = rollRepository.findByClazzKeywordAndStudentKeyword(classKeyword, stuKeyword,
            PageRequest.of(pageNumber, pageSize, Sort.by(desc("classDate"), desc("inputDate"))));
        return rollList.map(r -> RollHistoryVo.builder()
            .studentName(r.getStudent().getFirstName() + " " + r.getStudent().getLastName())
            .className(r.getClazz().getName())
            .classDate(r.getClassDate())
            .isPresent("Y".equals(r.getIsPresent()))
            .creditRedeemed(r.getCreditRedeemed())
            .build());
    }
}
