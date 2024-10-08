package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    TeacherResponseDTO addTeacher(TeacherRequestDTO request);

    PageResponse<?> findAll(int page, int pageSize);

    List<TeacherResponseDTO> findAll();

    List<TeacherResponseDTO> filterTeacherByCourse(Long id);

    void addTeacherToCourse(UUID teacherId, Long courseId);

    List<TeacherResponseDTO> getTeacherByCourseId(Long courseId);
}
