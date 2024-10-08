package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.response.RegisterTrialDTO;
import com.techzen.techlearn.entity.StudentCourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterTrialMapper {
    @Mapping(source = "userEntity.id", target = "idUser")
    RegisterTrialDTO toRegisterTrialDTO(StudentCourseEntity entity);
}
