package com.techzen.techlearn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "courseService", url = "${application.urlClient}/courses")
public interface CourseClient {

    @GetMapping("/user")
    ResponseEntity<?> getAllCourseForUser(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int pageSize);

    @GetMapping("/list-id")
    ResponseEntity<?> getCourseByListId(@RequestParam List<Long> id);

    @GetMapping("/{id}/chapters")
    ResponseEntity<?> getChapterByIdCourse(@PathVariable Long id);

    @GetMapping("/{id}")
    ResponseEntity<?> getCourseById(@PathVariable Long id);

}