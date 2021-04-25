package com.taw.courses.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class Student {
    @NotBlank
    private String email;
    private LocalDateTime registerDate = LocalDateTime.now();
    private StudentStatus status;
}
//tylko te pola studenta, z których będę korzystał