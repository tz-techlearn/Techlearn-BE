package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "mentor", target = "isMentor")
    @Mapping(source = "teacher", target = "isTeacher")
    UserResponseDTO toUserResponseDTO(UserEntity userEntity);

    UserEntity toUserEntity(UserRequestDTO userRequestDTO);

    @Mapping(source = "points", target = "points")
    StudentCourseResponseDTO toStudentCourseResponseDTO(UserEntity userEntity);
}
