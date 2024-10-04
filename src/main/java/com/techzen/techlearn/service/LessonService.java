package com.techzen.techlearn.service;

import org.springframework.stereotype.Service;

@Service
public interface LessonService {

    Object getAllLessons(int page, int pageSize, Long idChapter);

    Object getLessonById(Long id);
}
