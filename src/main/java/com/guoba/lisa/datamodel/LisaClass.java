package com.guoba.lisa.datamodel;

import lombok.*;

import javax.persistence.*;

import java.time.LocalTime;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class LisaClass {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String weekday;
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalTime startTime;
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalTime endTime;
    @ManyToOne
    @JoinColumn(name = "institution_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Institution institution;
    @ManyToMany
    @JoinTable(name = "student_class", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students;
}
