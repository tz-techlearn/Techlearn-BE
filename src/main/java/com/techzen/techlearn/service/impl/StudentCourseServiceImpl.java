package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.StudentCourseMapper;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.service.StudentCourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentCourseServiceImpl implements StudentCourseService {

    StudenCourseRepository studenCourseRepository;
    StudentCourseMapper studentCourseMapper;

    @Override
    public List<StudentCourseResponseDTO> getByIdUser(UUID id) {
        var courseEntities = studenCourseRepository.findAllByUserEntityId(id);
        return courseEntities.stream()
                .map(studentCourseMapper::toDTO)
                .toList();
    }

    @Override
    public StudentCourseResponseDTO getByIdCourse(Long idCourse, UUID idUser) {
        var course = studenCourseRepository.findByIdCourseAndUserEntityId(idCourse, idUser)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return studentCourseMapper.toDTO(course);
    }
}
