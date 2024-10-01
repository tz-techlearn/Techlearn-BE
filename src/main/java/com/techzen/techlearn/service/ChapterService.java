package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface ChapterService {
    PageResponse<?> getAllChaptersByCourseId(Long courseId);
}
