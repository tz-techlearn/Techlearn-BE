package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.Role;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.RoleType;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.UserMapper;
import com.techzen.techlearn.repository.RoleRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;

    @Override
    public UserResponseDTO getUserById(UUID id) {
        UserEntity user = userRepository.findUserById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserEntityByAccessToken(String accessToken) {
        UserEntity user = userRepository.findUserEntityByAccessToken(accessToken).orElseThrow(()-> new AppException(ErrorCode.INVALID_TOKEN));
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO addUser(UserRequestDTO request) {
        UserEntity user = userMapper.toUserEntity(request);
        user.setIsDeleted(false);
        user = userRepository.save(user);
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO request) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var userMap = userMapper.toUserEntity(request);
        userMap.setId(id);
        userMap.setIsDeleted(false);
        return userMapper.toUserResponseDTO(userRepository.save(userMap));
    }

    @Override
    public void deleteUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public PageResponse<?> getAllUser(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<UserResponseDTO> list = users.map(userMapper::toUserResponseDTO).stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public UserResponseDTO addRole(UUID uniqueId, List<RoleType> roleTypes) {
        UserEntity user = userRepository.findById(uniqueId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        roleTypes.forEach(roleType -> {
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        });

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO retrieveUser() {
       var context = SecurityContextHolder.getContext();
       String email = context.getAuthentication().getName();

       UserEntity user = userRepository.findByEmail(email)
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

       return userMapper.toUserResponseDTO(user);
    }

    @Override
        public StudentCourseResponseDTO getAllPointsById(UUID idUser) {
            Integer totalPoints = userRepository.getAllPointsById(idUser);
            UserEntity entity = new UserEntity();
            entity.setPoints(totalPoints);
            StudentCourseResponseDTO dto = userMapper.toStudentCourseResponseDTO(entity);

            return StudentCourseResponseDTO.builder()
                    .points(dto.getPoints())
                    .build();
    }
}
