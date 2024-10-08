package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.RegisterTrialDTO;
import com.techzen.techlearn.entity.StudentCourseEntity;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.StudentCourseStatus;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.RegisterTrialMapper;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.RegisterTrialService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegisterTrialServiceImpl implements RegisterTrialService {

    StudenCourseRepository studenCourseRepository;
    RegisterTrialMapper registerTrialMapper;
    UserRepository userRepository;

    @Override
    public RegisterTrialDTO save(UUID idUser, long idCourse) {
        UserEntity userEntity = userRepository.findUserById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (studenCourseRepository.existUserIdAndIdCourse(idUser, idCourse)
        ) {
            throw new AppException(ErrorCode.Student_course_exist);
        }
        return registerTrialMapper.toRegisterTrialDTO(studenCourseRepository.
                save(StudentCourseEntity.builder()
                        .idCourse(idCourse)
                        .isDeleted(false)
                        .userEntity(userEntity)
                        .status(StudentCourseStatus.TRIAL)
                        .build()));
    }


}
