package com.guoba.lisa.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.ZonedDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Data
public class Renew {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private LisaClass clazz;
    private ZonedDateTime date;
    private ZonedDateTime inputDate;
    private Integer currentCredit;
    private Integer newCredit;
}
