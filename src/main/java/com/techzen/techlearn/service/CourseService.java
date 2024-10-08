package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.CourseResponseDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CourseService {

    CourseResponseDTO findById(long idCourse);

    PageResponse<?> getCoursesByUserId(UUID userId, int page, int pageSize);

    List<UserResponseDTO> findUserByCourse(long id);

    List<TeacherResponseDTO> findTeacherByCourse(long id);

    Object getAllCourse(int page, int pageSize);

    Object getCourseById(Long id);
}
