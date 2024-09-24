package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;

import java.util.List;

public interface TeacherService {
    TeacherResponseDTO addTeacher(TeacherRequestDTO request);

    PageResponse<?> findAll(int page, int pageSize);

    List<TeacherResponseDTO> findAll();

    List<TeacherResponseDTO> filterTeacherByCourse(Long id);
}
