package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;

import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Institution {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(name = "user_institution", joinColumns = @JoinColumn(name = "institution_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<LisaUser> users;
    @OneToMany(mappedBy = "institution")
    private Set<LisaClass> classes;
}
