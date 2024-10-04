package com.techzen.techlearn.controller;

import com.techzen.techlearn.service.StudentCourseService;
import com.techzen.techlearn.util.JsonResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/student-courses")
public class StudentCourseController {

    StudentCourseService studentCourseService;

    @GetMapping
    public ResponseEntity<?> getAllByIdUser(@RequestParam UUID id) {
        return JsonResponse.ok(studentCourseService.getByIdUser(id));
    }

}
