package com.guoba.lisa.web.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddStudent {
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private LocalDate enrolmentDate;
    private Integer credits;

    private Boolean parentInfo;
    private Long parentId;
    private String parentFirstName;
    private String parentLastName;
    private String contactNumber;
}
