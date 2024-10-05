package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.BuyCourseResponseDTO;
import com.techzen.techlearn.entity.StudentCourseEntity;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.StudentCourseStatus;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.BuyCourseMapper;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.BuyCourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuyCourseServiceImpl implements BuyCourseService {

    StudenCourseRepository studenCourseRepository;
    UserRepository userRepository;
    BuyCourseMapper buyCourseMapper;


    @Override
    public BuyCourseResponseDTO buyCourse(UUID id, long id_course) {
        UserEntity user = userRepository.findUserById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (studenCourseRepository.existUserIdAndIdCourse(id, id_course)
        ) {
            StudentCourseEntity studentCourseEntity = studenCourseRepository.findByUserEntityIdAndIdCourse(id, id_course);
            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
                    .id(studentCourseEntity.getId())
                    .idCourse(id_course)
                    .userEntity(user)
                    .status(StudentCourseStatus.PAID)
                    .isDeleted(false)
                    .build()));
        }else{
            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
                    .idCourse(id_course)
                    .userEntity(user)
                    .status(StudentCourseStatus.PAID)
                    .isDeleted(false)
                    .build()));
        }
    }
}
