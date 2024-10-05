package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.CourseService;
import com.techzen.techlearn.util.JsonResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
public class CourseController {

    CourseService courseService;

    @GetMapping("/user")
    public ResponseEntity<?> getCourseByUser(@RequestParam(required = false, defaultValue = "1") int page,
                                             @RequestParam(required = false, defaultValue = "30") int pageSize,
                                             @RequestParam UUID id) {
        return JsonResponse.ok(courseService.getCoursesByUserId(id, page, pageSize));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourse(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(courseService.getAllCourse(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return JsonResponse.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/users")
    public ResponseData<?> findUserByCourse(@PathVariable long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(courseService.findUserByCourse(id))
                .build();
    }

    @GetMapping("/{id}/teachers")
    public ResponseData<?> findTeacherByCourse(@PathVariable long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(courseService.findTeacherByCourse(id))
                .build();
    }
}
