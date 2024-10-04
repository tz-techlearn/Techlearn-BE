package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.client.LessonClient;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonServiceImpl implements LessonService {

    LessonClient lessonClient;

    @Override
    public Object getAllLessons(int page, int pageSize, Long idChapter) {
        return lessonClient.getAllLesson(page, pageSize, idChapter).getBody();
    }

    @Override
    public Object getLessonById(Long id) {
        var assignment = lessonClient.getLessonById(id);
        return assignment.getBody();
    }
}
