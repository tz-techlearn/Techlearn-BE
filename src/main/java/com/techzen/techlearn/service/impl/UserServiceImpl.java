package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.client.PointClient;
import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.*;
import com.techzen.techlearn.entity.Role;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.StudentCourseResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.*;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.RoleType;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.UserMapper;
import com.techzen.techlearn.repository.MentorRepository;
import com.techzen.techlearn.repository.RoleRepository;
import com.techzen.techlearn.repository.TeacherRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.MailService;
import com.techzen.techlearn.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    MailService gmaMailService;
    PointClient pointClient;
    MentorRepository mentorRepository;
    TeacherRepository teacherRepository;

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

//    @Override
//    public UserResponseDTO retrieveUser() {
//       var context = SecurityContextHolder.getContext();
//       String email = context.getAuthentication().getName();
//
//       UserEntity user = userRepository.findByEmail(email)
//               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//       return userMapper.toUserResponseDTO(user);
//
//    }


    @Override
    public UserResponseDTO retrieveUser() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserResponseDTO userResponseDTO = userMapper.toUserResponseDTO(user);

        if (user.isMentor()) {
            Mentor mentor = mentorRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.MENTOR_NOT_EXISTED));
            List<ChapterEntity> chapters = mentor.getChapters();
            userResponseDTO.setChapters(chapters);
        }

        if (user.isTeacher()) {
            Teacher teacher = teacherRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
            List<CourseEntity> courses = teacher.getCourses();
            userResponseDTO.setCourses(courses);
        }

        if (!user.isMentor() && !user.isTeacher()) {
            List<CourseEntity> userCourses = user.getCourseEntities();
            userResponseDTO.setCourses(userCourses);
        }

        return userResponseDTO;
    }

    public UserResponseDTO updateUserMe(UserResponseDTO userResponseDTO) {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(userResponseDTO.getFullName());
        user.setAge(userResponseDTO.getAge());
        user.setEmail(userResponseDTO.getEmail());
        user.setPoints(userResponseDTO.getPoints());
        user.setAvatar(userResponseDTO.getAvatar());

        if (user.isTeacher()) {
            Teacher teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
            teacher.setEmail(userResponseDTO.getEmail());
            teacherRepository.save(teacher);
        }

        if (user.isMentor()) {
            Mentor mentor = mentorRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.MENTOR_NOT_EXISTED));
            mentor.setEmail(userResponseDTO.getEmail());
            mentorRepository.save(mentor);
        }
        userRepository.save(user);
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

    @Override
    public UserResponseDTO removeRoles(UUID id, List<RoleType> roles) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        roles.forEach(roleType -> {
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            if (user.getRoles().contains(role)) {
                user.getRoles().remove(role);
            }
        });

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public PointResponseDTO requestPointsPurchase(PointResponseDTO dto) throws MessagingException {
        UserResponseDTO user = retrieveUser();
//        UserEntity existingUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        existingUser.setPoints(existingUser.getPoints() + Integer.parseInt(dto.getPoints()));
//        userRepository.save(existingUser);
        gmaMailService.sendMailSupportPoints(dto, user);
        return dto;
    }


    @Override
    public PageResponse<?> findAllPointsPackage(int page, int pageSize) {
        var response = pointClient.findAllPointsPackage(page, pageSize);
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .items(response.getBody())
                .build();
    }

}