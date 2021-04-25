package com.taw.courses.exception;

public enum CourseError {
    COURSE_NOT_FOUND("Course not found"),
    INVALID_DATE("Invalid date"),
    PARTICIPANTS_LIMIT("Participants limit reached"),
    WRONG_STATUS("Wrong course status"),
    WRONG_STUDENT_STATUS("Wrong student status"),
    COURSE_PARTICIPANTS_LIMIT("Course participants limit reached"),
    STUDENT_ALREADY_PARTICIPATES("Student already participates in this course"),
    STUDENT_CANNOT_BE_REGISTERED("Student cannot be registered");

    private String message;

    CourseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
