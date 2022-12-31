package com.guoba.lisa.datamodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;


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
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(columnDefinition = "integer not null default 0")
    private Integer status = 0;
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(columnDefinition = "integer not null default 1")
    private Integer sessionCredits = 1;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Institution institution;
    @ManyToMany
    @JoinTable(name = "student_class", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> students;
}
