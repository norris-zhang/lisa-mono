package com.guoba.lisa.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import static jakarta.persistence.GenerationType.IDENTITY;


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
