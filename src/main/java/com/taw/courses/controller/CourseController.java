package com.taw.courses.controller;

import com.taw.courses.model.Course;
import com.taw.courses.model.Status;
import com.taw.courses.model.dto.CourseMemberData;
import com.taw.courses.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) Status status) {
        return courseService.getCourses(status);
    }

    @PostMapping
    public Course addCourse(@RequestBody @Valid Course course) {
        return courseService.addCourse(course);
    }

    @GetMapping("/{code}")
    public Course getCourse(@PathVariable String code) {
        return courseService.getCourse(code);
    }

    @DeleteMapping("/{code}")
    void deleteCourse(String code) {
        courseService.deleteCourse(code);
    }

    @PutMapping("/{code}")
    Course putCourse(@PathVariable String code, @RequestBody Course course) {
        return courseService.putCourse(code, course);
    }

    @PatchMapping("/{code}")
    Course patchCourse(@PathVariable String code, @RequestBody Course course) {
        return courseService.patchCourse(code, course);
    }

    @PostMapping("/{code}/student/{studentId}")
    public ResponseEntity<?> registerStudent(@PathVariable String code, @PathVariable Long studentId) {
        courseService.registerStudent(code, studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{code}/members")
    public List<CourseMemberData> findCourseMembers(@PathVariable String code) {
        return courseService.findStudentsByEmailList(code);
    }

    @PatchMapping("/{code}/finish-enroll")
    public ResponseEntity<?> finishEnroll(@PathVariable String code) {
        courseService.finishEnroll(code);
        return ResponseEntity.ok().build();
    }

}
