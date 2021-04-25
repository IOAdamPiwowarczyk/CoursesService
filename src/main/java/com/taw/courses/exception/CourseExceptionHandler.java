package com.taw.courses.exception;

import feign.FeignException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseExceptionHandler {

    @ExceptionHandler(value = CourseException.class)
    public ResponseEntity<com.taw.courses.exception.ErrorInfo> handleException(CourseException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (CourseError.COURSE_NOT_FOUND.equals(e.getCourseError())) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (CourseError.INVALID_DATE.equals(e.getCourseError())
                || CourseError.STUDENT_CANNOT_BE_REGISTERED.equals(e.getCourseError())) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (CourseError.PARTICIPANTS_LIMIT.equals(e.getCourseError())
                || CourseError.WRONG_STATUS.equals(e.getCourseError())
                || CourseError.COURSE_PARTICIPANTS_LIMIT.equals(e.getCourseError())
                || CourseError.STUDENT_ALREADY_PARTICIPATES.equals(e.getCourseError())) {
            httpStatus = HttpStatus.CONFLICT;
        }

        return ResponseEntity.status(httpStatus)
                .body(new com.taw.courses.exception.ErrorInfo(e.getCourseError().getMessage()));
    }

    @ExceptionHandler(value = FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException e) {
        return ResponseEntity.status(e.status()).body(new JSONObject(e.contentUTF8()).toMap());
    }
}