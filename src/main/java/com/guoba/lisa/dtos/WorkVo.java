package com.guoba.lisa.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkVo {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private Long picId;
    private String path;
    private String mimetype;
}
