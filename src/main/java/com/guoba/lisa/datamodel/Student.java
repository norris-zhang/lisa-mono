package com.guoba.lisa.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Student {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String firstName;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String lastName;
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDate dateOfBirth;
    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDate enrolledOn;
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer credits;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Institution institution;
    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private Set<LisaClass> classes;
    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private Set<ParentStudent> parents;
    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private Set<Work> works;
}
