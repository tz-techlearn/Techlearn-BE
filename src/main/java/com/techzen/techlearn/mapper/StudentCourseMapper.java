package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import com.techzen.techlearn.entity.StudentCourseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentCourseMapper {

    StudentCourseResponseDTO toDTO(StudentCourseEntity studentCourseEntity);
}
