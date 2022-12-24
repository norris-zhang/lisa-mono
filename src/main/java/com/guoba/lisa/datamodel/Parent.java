package com.guoba.lisa.datamodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

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
