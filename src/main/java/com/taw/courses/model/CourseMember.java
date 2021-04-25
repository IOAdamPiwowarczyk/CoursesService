package com.taw.courses.model;

import com.taw.courses.model.dto.Student;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CourseMember {
    @NotBlank
    private String email;
    private LocalDateTime registerDate = LocalDateTime.now();

    public CourseMember() {}
    public CourseMember(Student student) {
        this.email = student.getEmail();
        this.registerDate = student.getRegisterDate();
    }
}
