package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.CourseResponseDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.SubmittionResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.CourseEntity;
import com.techzen.techlearn.entity.SubmitionEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.CourseMapper;
import com.techzen.techlearn.mapper.UserMapper;
import com.techzen.techlearn.repository.CourseRepository;
import com.techzen.techlearn.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public PageResponse<?> getCoursesByUserId(UUID userId, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<CourseEntity> reviews =courseRepository.findAllByUserEntitiesId(userId, pageable);
        List<CourseResponseDTO> list = reviews.stream().map(courseMapper::toCourseResponseDTO).collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(reviews.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public CourseResponseDTO findCourseById(long id) {
        return courseMapper.toCourseResponseDTO(courseRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.COURSE_NOT_FOUND)));
    }

    @Override
    public List<UserResponseDTO> findUserByCourse(long id) {
        return courseRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.COURSE_NOT_FOUND))
                .getUserEntities()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO findById(long idCourse) {
        return courseMapper
                .toCourseResponseDTO(courseRepository
                        .findById(idCourse)
                        .orElseThrow(()->new RuntimeException(" COURSE_NOT_FOUND")));
    }

}
