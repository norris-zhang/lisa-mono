package com.guoba.lisa.datamodel;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
public class Picture {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String fileName;
    private String extension;
    private String mimetype;
    private String path;
}
