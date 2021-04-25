package com.taw.courses.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//@Component // nie potrzebne jednak, obsługa w CourseExcetionHandler
public class AppFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (HttpStatus.valueOf(response.status()).is4xxClientError()) {
            return new com.taw.courses.exception.CourseException(com.taw.courses.exception.CourseError.STUDENT_CANNOT_BE_REGISTERED);
        }
        return defaultErrorDecoder.decode(s, response);
    }
}
