package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.RegisterTrialDTO;
import com.techzen.techlearn.entity.CourseEntity;
import com.techzen.techlearn.entity.StudentCourseEntity;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.StudentCourseStatus;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.RegisterTrialMapper;
import com.techzen.techlearn.repository.CourseRepository;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.RegisterTrialService;
import jakarta.mail.MessagingException;
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
    CourseRepository courseRepository;
    RegisterTrialMapper registerTrialMapper;
    UserRepository userRepository;
    GmailServiceImpl gmailService;

    @Override
    public RegisterTrialDTO save(UUID idUser, long idCourse) throws MessagingException {
        UserEntity userEntity = userRepository.findUserById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (studenCourseRepository.existUserIdAndIdCourse(idUser, idCourse)
        ) {
            throw new AppException(ErrorCode.Student_course_exist);
        }
        CourseEntity courseEntity = courseRepository.findById(idCourse).orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));
        gmailService.trialCourseMail(userEntity.getFullName(),courseEntity.getName() , userEntity.getEmail(), "Hoàn thành đăng kí dùng thử khóa học", "Hoàn thành đăng kí thử khóa học", "Description hoàn thành đăng kí thử khóa học");
        return registerTrialMapper.toRegisterTrialDTO(studenCourseRepository.
                save(StudentCourseEntity.builder()
                        .idCourse(idCourse)
                        .isDeleted(false)
                        .userEntity(userEntity)
                        .status(StudentCourseStatus.TRIAL)
                        .build()));
    }


}
