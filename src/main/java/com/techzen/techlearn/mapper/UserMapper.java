package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "roles", target = "roles")
    UserResponseDTO toUserResponseDTO(UserEntity userEntity);

    UserEntity toUserEntity(UserRequestDTO userRequestDTO);

}
