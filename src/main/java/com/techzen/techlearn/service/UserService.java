package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.*;
import com.techzen.techlearn.enums.RoleType;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    UserResponseDTO getUserById(UUID id);
    UserResponseDTO getUserEntityByAccessToken(String accessToken);

    UserResponseDTO addUser(UserRequestDTO request);

    UserResponseDTO updateUser(UUID id, UserRequestDTO request);

    void deleteUser(UUID id);

    PageResponse<?> getAllUser(int page, int pageSize);

    UserResponseDTO addRole(UUID uniqueId, List<RoleType> roleTypes);

    UserResponseDTO retrieveUser();

    StudentCourseResponseDTO getAllPointsById (UUID idUser);

    UserResponseDTO removeRoles(UUID id, List<RoleType> roles);

    PointResponseDTO requestPointsPurchase(PointResponseDTO dto) throws MessagingException;

    PageResponse<?> findAllPointsPackage(int page, int pageSize);
  
    UserResponseDTO updateUserMe(UserResponseDTO userResponseDTO);
}
