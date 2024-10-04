package com.techzen.techlearn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "assignmentService", url = "${application.urlClient}/lessons")
public interface LessonClient {

    @GetMapping
    ResponseEntity<?> getAllLesson(@RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "10") int pageSize,
                                   @RequestParam Long idChapter);

    @GetMapping("/{id}")
    ResponseEntity<?> getLessonById(@PathVariable Long id);

}