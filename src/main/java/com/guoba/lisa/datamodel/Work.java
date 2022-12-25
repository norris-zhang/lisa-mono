package com.guoba.lisa.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Work {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;
    @ToString.Include
    @EqualsAndHashCode.Include
    private String title;
    @ToString.Include
    @EqualsAndHashCode.Include
    private String description;
    @ToString.Include
    @EqualsAndHashCode.Include
    private LocalDate date;
    @ToString.Include
    @EqualsAndHashCode.Include
    private ZonedDateTime uploadDate;
    @ManyToOne
    @JoinColumn(name = "student_id")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Student student;
    @ManyToOne
    @JoinColumn(name = "picture_id")
    @ToString.Include
    @EqualsAndHashCode.Include
    private Picture picture;
    @ManyToOne
    @JoinColumn(name = "class_id")
    @ToString.Include
    @EqualsAndHashCode.Include
    private LisaClass clazz;
}
