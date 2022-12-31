package com.guoba.lisa.services;

import com.guoba.lisa.datamodel.Roll;
import com.guoba.lisa.datamodel.Student;
import com.guoba.lisa.dtos.ReportStudentVo;
import com.guoba.lisa.dtos.ReportVo;
import com.guoba.lisa.enums.StatPeriod;
import com.guoba.lisa.repositories.RenewRepository;
import com.guoba.lisa.repositories.RollRepository;
import com.guoba.lisa.repositories.StudentRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
public class ReportService {
    @Value("${low.credit.threshold}")
    private Integer lowCreditThreshold;
    private final RollRepository rollRepository;
    private final RenewRepository renewRepository;
    private final StudentRepository studentRepository;

    public ReportService(RollRepository rollRepository, RenewRepository renewRepository, StudentRepository studentRepository) {
        this.rollRepository = rollRepository;
        this.renewRepository = renewRepository;
        this.studentRepository = studentRepository;
    }

    public ReportVo report(Long institutionId, Integer pvalue, LocalDate startDate, LocalDate endDate) {
        ReportVo vo = new ReportVo();

        StatPeriod statPeriod = StatPeriod.fromPvalue(pvalue);
        Pair<LocalDate, LocalDate> period = statPeriod.calcPeriod(startDate, endDate);

        List<Roll> rolls = rollRepository.findByClassDateBetweenAndClazzInstitutionId(period.getLeft(), period.getRight(), institutionId);
        Set<Long> classIds = new HashSet<>();
        Set<Long> studentIds = new HashSet<>();
        Set<String> classSessionKeys = new HashSet<>(); // classId:date - 2:2022-12-12
        int[] redeemedCredits = {0};
        rolls.forEach(r -> {
            Long stuId = r.getStudent().getId();
            Long classId = r.getClazz().getId();
            LocalDate classDate = r.getClassDate();
            classIds.add(classId);
            studentIds.add(stuId);
            String classSessionKey = classId + ":" + classDate.format(ofPattern("yyyy-MM-dd"));
            classSessionKeys.add(classSessionKey);
            redeemedCredits[0]+=r.getCreditRedeemed();
        });

        Integer sumOfCredits = studentRepository.findSumCreditsByInstitutionId(institutionId);

        Integer sumOfTopup = renewRepository.findSumTopupCreditsBetween(institutionId, period.getLeft(), period.getRight());

        List<ReportStudentVo> reportStudentVos = getLowCreditReportStudentVos(institutionId);

        vo.setActiveClasses(classIds.size());
        vo.setActiveStudents(studentIds.size());
        vo.setOwingCredits(sumOfCredits);
        vo.setStatPeriod(pvalue);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        vo.setGivenSessions(classSessionKeys.size());
        vo.setTopupCredits(sumOfTopup);
        vo.setRedeemedCredits(redeemedCredits[0]);
        vo.setAvgCreditsPerSession(classSessionKeys.isEmpty()
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(redeemedCredits[0] * 1.0 / classSessionKeys.size()));
        vo.setLowCreditStudents(reportStudentVos);
        return vo;
    }

    private List<ReportStudentVo> getLowCreditReportStudentVos(Long institutionId) {
        List<Student> lowCreditStudents = studentRepository.findByInstitutionIdAndCreditsLessThan(institutionId, lowCreditThreshold, Sort.by(Direction.ASC, "credits"));
        return lowCreditStudents.stream().map(s -> {
            ReportStudentVo rsv = new ReportStudentVo();
            rsv.setId(s.getId());
            rsv.setName(s.getFirstName() + " " + s.getLastName());
            rsv.setCredits(s.getCredits());
            rsv.setParentName(s.getParents().stream()
                .map(p -> p.getParent().getFirstName() + " " + p.getParent().getLastName())
                .collect(Collectors.joining(" | ")));
            rsv.setParentContact(s.getParents().stream()
                .map(p -> p.getParent().getContactNumber())
                .collect(Collectors.joining(" | ")));
            if (!s.getRenews().isEmpty()) {
                rsv.setLastTopup(s.getRenews().get(0).getDate());
                rsv.setLastTopupAmount(s.getRenews().get(0).getTopupAmount());
            }
            return rsv;
        }).collect(Collectors.toList());
    }
}
