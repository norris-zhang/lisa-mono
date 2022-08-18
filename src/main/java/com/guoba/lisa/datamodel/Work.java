package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Work {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private ZonedDateTime uploadDate;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;
}
