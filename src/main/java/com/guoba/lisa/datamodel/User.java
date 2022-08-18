package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;
    private String username;
    private String password;
    private String role;
    @ManyToMany(mappedBy = "users")
    private Set<Institution> institutions;
}
