package com.guoba.lisa.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Student {
    @Transient
    private PersistentAttributeInterceptor interceptor;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "institution_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Institution institution;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private LisaUser user;
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
