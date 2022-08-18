package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Parent {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String contactNumber;
    @OneToMany(mappedBy = "parent")
    private Set<ParentStudent> students;
}
