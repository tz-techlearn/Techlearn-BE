package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.BuyCourseResponseDTO;

import java.util.UUID;

public interface BuyCourseService{
    public BuyCourseResponseDTO buyCourse(UUID id, long id_course);
}
