package com.taw.courses.service;

import com.taw.courses.model.dto.CourseMemberData;
import com.taw.courses.model.dto.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "STUDENT-SERVICE")//korzysta z ribbona i eureki, dlatego nie trzeba podawać url
//Feign client z wykorzystaniem ribbona będzie przekazywał żądanie na najmniej obciążoną instancję (gdyby było ich więcej)
@RequestMapping("/students")
public interface StudentServiceClient {
    //student-service na endpoint /students i oczekuje listy studentów
    @GetMapping("/{id}")
    Student getStudent(@PathVariable Long id);

    @PostMapping("/emails")
    List<CourseMemberData> getStudentsByEmailList(@RequestBody List<String> emails) ;
}


//wew architektury nie ma potrzeby korzystać z gateway'a. serwisy, które razem tworzą architekturę mikroserwisów są zaufane
//tak będzie szybciej i jest bezpiecznie; gateway jest wykorzystywany do połączeń z zew architektóry mikroserwisów
