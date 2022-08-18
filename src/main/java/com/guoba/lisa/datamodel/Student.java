package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer credits;
    @ManyToMany(mappedBy = "students")
    private Set<LisaClass> classes;
    @OneToMany(mappedBy = "student")
    private Set<ParentStudent> parents;
    @OneToMany(mappedBy = "student")
    private Set<Work> works;
}
