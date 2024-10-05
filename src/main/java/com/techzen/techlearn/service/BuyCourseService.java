package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.BuyCourseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface BuyCourseService{
    public BuyCourseResponseDTO buyCourse(UUID id, long id_course);
}
