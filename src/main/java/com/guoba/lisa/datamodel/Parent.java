package com.guoba.lisa.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Parent {
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
    private String contactNumber;
    @OneToMany(mappedBy = "parent")
    private Set<ParentStudent> students;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Institution institution;
}
