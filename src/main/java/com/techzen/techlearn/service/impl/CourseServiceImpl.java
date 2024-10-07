package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.client.CourseClient;
import com.techzen.techlearn.dto.response.CourseResponseDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.CourseMapper;
import com.techzen.techlearn.mapper.TeacherMapper;
import com.techzen.techlearn.mapper.UserMapper;
import com.techzen.techlearn.repository.CourseRepository;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;
    CourseMapper courseMapper;
    UserMapper userMapper;
    TeacherMapper teacherMapper;
    CourseClient courseClient;
    StudenCourseRepository studenCourseRepository;

    @Override
    public PageResponse<?> getCoursesByUserId(UUID userId, int page, int pageSize) {
        var courseIds = studenCourseRepository.findAllCourseIdsByUserId(userId);
        var course = courseClient.getCourseByListId(courseIds);
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(0)
                .items(course.getBody())
                .build();
    }

    @Override
    public List<UserResponseDTO> findUserByCourse(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND))
                .getUserEntities()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherResponseDTO> findTeacherByCourse(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND))
                .getTeachers()
                .stream()
                .map(teacherMapper::toTeacherResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Object getAllCourse(int page, int pageSize) {
        return courseClient.getAllCourses(page, pageSize).getBody();
    }

    @Override
    public Object getCourseById(Long id) {
        return courseClient.getCourseById(id).getBody();
    }

    @Override
    public CourseResponseDTO findById(long idCourse) {
        return courseMapper.toCourseResponseDTO(courseRepository.findById(idCourse)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
    }

}
