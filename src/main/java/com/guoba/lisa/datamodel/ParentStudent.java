package com.guoba.lisa.datamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ParentStudent {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String relationship;
}
