package com.taw.courses.repository;

import com.taw.courses.model.Course;
import com.taw.courses.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//Tutaj nie dziedziczymy z jpa, bo mongo db nie jest bazą relazyjną
//@Repository//nie trzeba pisać, bo dziedziczy po mongo repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findAllByStatus(Status status);
}
