package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Roll {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private LisaClass clazz;
    private LocalDate classDate;
    private ZonedDateTime inputDate;
}
