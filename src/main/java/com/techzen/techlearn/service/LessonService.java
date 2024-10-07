package com.techzen.techlearn.service;

import org.springframework.stereotype.Service;

@Service
public interface LessonService {

    Object getAllLessons(int page, int pageSize);

    Object getLessonById(Long id);
}
