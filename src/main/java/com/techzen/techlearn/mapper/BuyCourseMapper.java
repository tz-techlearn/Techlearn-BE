package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.response.BuyCourseResponseDTO;
import com.techzen.techlearn.entity.StudentCourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuyCourseMapper {

    @Mapping(source = "userEntity.id", target = "idUser")
    BuyCourseResponseDTO toBuyCourseResponeDTO(StudentCourseEntity studentCourseEntity);

}
