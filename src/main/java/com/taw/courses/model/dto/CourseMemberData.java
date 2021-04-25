package com.taw.courses.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseMemberData {
    private String firstName;
    private String lastName;
    private String email;

    public CourseMemberData() {
    }

    public CourseMemberData(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
