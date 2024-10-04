package com.techzen.techlearn.controller;

import com.techzen.techlearn.service.LessonService;
import com.techzen.techlearn.util.JsonResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons")
public class LessonController {

    LessonService lessonService;

    @GetMapping
    public ResponseEntity<?> getAllLesson(@RequestParam(defaultValue = "1", required = false) int page,
                                              @RequestParam(defaultValue = "10", required = false) int pageSize,
                                              @RequestParam Long idChapter) {
        return JsonResponse.ok(lessonService.getAllLessons(page, pageSize, idChapter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable long id) {
        return JsonResponse.ok(lessonService.getLessonById(id));
    }
}
