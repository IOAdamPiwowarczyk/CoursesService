package com.taw.courses.model;

import com.taw.courses.exception.CourseError;
import com.taw.courses.exception.CourseException;
import com.taw.courses.model.dto.Student;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Adnotacja @Document zamiast @Entity, bo to jest mongodb, będzie to kolekcja dokumentów, nie encje
@Document
@Getter
@Setter
public class Course {

    @Id
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Future
    private LocalDateTime startDate;
    @NotNull
    @Future
    private LocalDateTime endDate;
    @Min(0)
    private Long participantsLimit;
    @NotNull
    @Min(0)
    private Long participantsNumber;
    @NotNull//W przypadku mongo db nie daje się @Enumerated
    private Status status;

    private List<CourseMember> members = new ArrayList<>();

    public void validateCourse() {
        validateDates();
        validateParticipantsNumber();
        validateCourseStatus();
    }

    public void incrementParticipantNumber() {
        participantsNumber++;
        if (participantsNumber.equals(participantsLimit)) {
            setStatus(Status.FULL);
        }
    }

    public void addMember(Student student) {
        CourseMember member = new CourseMember(student);
        members.add(member);
    }

    private void validateDates() {
        if (startDate == null || endDate == null) {
            return;
        }
        if (startDate.isAfter(endDate)) {
            throw new CourseException(CourseError.INVALID_DATE);
        }
    }

    private void validateParticipantsNumber() {
        if (participantsNumber == null || participantsLimit == null) {
            return;
        }
        if (participantsNumber > participantsLimit) {
            throw new CourseException(CourseError.PARTICIPANTS_LIMIT);
        }
    }

    private void validateCourseStatus() {
        if (status == null) {
            return;
        }
        if (Status.FULL.equals(status) && !participantsNumber.equals(participantsLimit)) {
            throw new CourseException(CourseError.WRONG_STATUS);
        }
        if (Status.ACTIVE.equals(status) && participantsNumber.equals(participantsLimit)) {
            throw new CourseException(CourseError.WRONG_STATUS);
        }
    }
}