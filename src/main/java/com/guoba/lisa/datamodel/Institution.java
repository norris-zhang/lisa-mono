package com.guoba.lisa.datamodel;

import lombok.*;

import javax.persistence.*;

import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Institution {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;
    @ManyToMany
    @JoinTable(name = "user_institution", joinColumns = @JoinColumn(name = "institution_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<LisaUser> users;
    @OneToMany(mappedBy = "institution")
    private Set<LisaClass> classes;
}
