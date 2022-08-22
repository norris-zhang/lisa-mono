package com.guoba.lisa.datamodel;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Roll {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @ToString.Include
    @EqualsAndHashCode.Include
    private LisaClass clazz;

    @ToString.Include
    @EqualsAndHashCode.Include
    private LocalDate classDate;

    @ToString.Include
    private ZonedDateTime inputDate;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String isPresent;

    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer creditBalance;
}
