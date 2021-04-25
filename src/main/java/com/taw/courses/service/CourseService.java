package com.taw.courses.service;

import com.taw.courses.model.Course;
import com.taw.courses.model.Status;
import com.taw.courses.model.dto.CourseMemberData;

import java.util.List;

public interface CourseService {
    List<Course> getCourses(Status status);

    Course getCourse(String code);

    Course addCourse(Course course);

    void deleteCourse(String code);

    Course putCourse(String code, Course course);

    Course patchCourse(String code, Course course);

    void registerStudent(String code, Long studentId);

    List<CourseMemberData> findStudentsByEmailList(String code);

    void finishEnroll(String code);
}
