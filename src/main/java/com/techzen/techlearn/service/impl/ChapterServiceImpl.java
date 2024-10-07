package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.client.CourseClient;
import com.techzen.techlearn.service.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterServiceImpl implements ChapterService {

    CourseClient courseClient;

    @Override
    public Object getAllChaptersByCourseId(Long courseId) {
        return courseClient.getChapterByIdCourse(courseId).getBody();
    }
}
