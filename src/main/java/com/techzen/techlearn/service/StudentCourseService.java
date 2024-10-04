package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface StudentCourseService {
    List<StudentCourseResponseDTO> getByIdUser(UUID id);
}
