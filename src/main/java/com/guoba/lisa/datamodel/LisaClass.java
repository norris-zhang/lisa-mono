package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalTime;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class LisaClass {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String weekday;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
    @ManyToMany
    @JoinTable(name = "student_class", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students;
}
