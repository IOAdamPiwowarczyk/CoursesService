package com.taw.courses.service;

import com.taw.courses.exception.CourseError;
import com.taw.courses.exception.CourseException;
import com.taw.courses.model.Course;
import com.taw.courses.model.CourseMember;
import com.taw.courses.model.Status;
import com.taw.courses.model.dto.CourseMemberData;
import com.taw.courses.model.dto.NotificationInfoDto;
import com.taw.courses.model.dto.Student;
import com.taw.courses.model.dto.StudentStatus;
import com.taw.courses.repository.CourseRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements com.taw.courses.service.CourseService {
    public static final String EXCHANGE_FINISH_ENROLL = "finish_enroll";
    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public CourseServiceImpl(CourseRepository courseRepository, StudentServiceClient studentServiceClient, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public List<Course> getCourses(Status status) {
        if (status != null) {
            return courseRepository.findAllByStatus(status);
        }
        return courseRepository.findAll();
    }

    @Override
    public Course getCourse(String code) {
        return courseRepository.findById(code).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course addCourse(Course course) {
        course.validateCourse();
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String code) {
        Course course = courseRepository.findById(code)
                .orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
        course.setStatus(Status.INACTIVE);
        courseRepository.save(course);
    }

    @Override
    public Course putCourse(String code, Course course) {
        return courseRepository.findById(code)
                .map(courseFromDb -> {
                    course.validateCourse();
                    courseFromDb.setName(course.getName());
                    courseFromDb.setDescription(course.getDescription());
                    courseFromDb.setStartDate(course.getStartDate());
                    courseFromDb.setEndDate(course.getEndDate());
                    courseFromDb.setParticipantsLimit(course.getParticipantsLimit());
                    courseFromDb.setParticipantsNumber(course.getParticipantsNumber());
                    courseFromDb.setStatus(course.getStatus());
                    return courseRepository.save(courseFromDb);
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course patchCourse(String code, Course course) {
        return courseRepository.findById(code)
                .map(courseFromDb -> {
                    course.validateCourse();
                    if (!StringUtils.isEmpty(course.getName())) {
                        courseFromDb.setName(course.getName());
                    }
                    if (!StringUtils.isEmpty(course.getDescription())) {
                        courseFromDb.setDescription(course.getDescription());
                    }
                    if (course.getStartDate() != null) {
                        courseFromDb.setStartDate(course.getStartDate());
                    }
                    if (course.getEndDate() != null) {
                        courseFromDb.setEndDate(course.getEndDate());
                    }
                    if (course.getParticipantsLimit() != null) {
                        courseFromDb.setParticipantsLimit(course.getParticipantsLimit());
                    }
                    if (course.getParticipantsNumber() != null) {
                        courseFromDb.setParticipantsNumber(course.getParticipantsNumber());
                    }
                    if (course.getStatus() != null) {
                        courseFromDb.setStatus(course.getStatus());
                    }
                    return courseRepository.save(courseFromDb);
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public void registerStudent(String code, Long studentId) {
        Student student = studentServiceClient.getStudent(studentId);
        validateStudentStatus(student);

        Course course = getCourse(code);
        validateCourse(student, course);

        registerStudent(student, course);
    }

    @Override
    public List<CourseMemberData> findStudentsByEmailList(String code) {
        Course course = getCourse(code);
        List<String> emails = course.getMembers().stream().map(CourseMember::getEmail).collect(Collectors.toList());
        return studentServiceClient.getStudentsByEmailList(emails);
    }

    @Override
    public void finishEnroll(String code) {
        Course course = getCourse(code);
        if (Status.INACTIVE.equals(course.getStatus())) {
            throw new CourseException(CourseError.WRONG_STATUS);
        }
        course.setStatus(Status.INACTIVE);
        courseRepository.save(course);
        sendMessageToRabbitMq(course);
    }

    private void sendMessageToRabbitMq(Course course) {
        List<String> emails = course.getMembers().stream().map(CourseMember::getEmail).collect(Collectors.toList());
        NotificationInfoDto notificationInfo = new NotificationInfoDto();
        notificationInfo.setCourseCode(course.getCode());
        notificationInfo.setCourseName(course.getName());
        notificationInfo.setCourseDescription(course.getDescription());
        notificationInfo.setCourseStartTime(course.getStartDate());
        notificationInfo.setCourseEndTime(course.getEndDate());
        notificationInfo.setEmails(emails);

        rabbitTemplate.convertAndSend(EXCHANGE_FINISH_ENROLL, notificationInfo);
    }

    private void validateStudentStatus(Student student) {
        if (!StudentStatus.ACTIVE.equals(student.getStatus())) {
            throw new CourseException(CourseError.WRONG_STUDENT_STATUS);
        }
    }

    private void validateCourse(Student student, Course course) {
        if (!Status.ACTIVE.equals(course.getStatus())) {
            throw new CourseException(CourseError.WRONG_STATUS);
        }
        if (Status.FULL.equals(course.getStatus())) {
            throw new CourseException(CourseError.COURSE_PARTICIPANTS_LIMIT);
        }
        if (course.getMembers().stream().anyMatch(student1 -> student1.getEmail().equals(student.getEmail()))) {
            throw new CourseException(CourseError.STUDENT_ALREADY_PARTICIPATES);
        }
    }

    private void registerStudent(Student student, Course course) {
        course.incrementParticipantNumber();
        course.addMember(student);
        courseRepository.save(course);
    }
}
