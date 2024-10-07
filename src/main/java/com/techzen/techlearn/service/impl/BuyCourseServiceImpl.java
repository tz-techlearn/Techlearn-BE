package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.response.BuyCourseResponseDTO;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.entity.CourseEntity;
import com.techzen.techlearn.entity.StudentCourseEntity;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.enums.StudentCourseStatus;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.BuyCourseMapper;
import com.techzen.techlearn.repository.CourseRepository;
import com.techzen.techlearn.repository.StudenCourseRepository;
import com.techzen.techlearn.repository.UserRepository;
import com.techzen.techlearn.service.BuyCourseService;
import com.techzen.techlearn.service.TeacherService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuyCourseServiceImpl implements BuyCourseService {

    StudenCourseRepository studenCourseRepository;
    UserRepository userRepository;
    BuyCourseMapper buyCourseMapper;
    GmailServiceImpl gmailService;
    TeacherService teacherService;
    CourseRepository courseRepository;

    @Override
    public BuyCourseResponseDTO buyCourse(UUID id, long id_course) throws MessagingException {
        UserEntity user = userRepository.findUserById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<TeacherResponseDTO> teacherResponses = teacherService.getTeacherByCourseId(id_course);
        CourseEntity course = courseRepository.findById(id_course).orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<String> teacherNames = new ArrayList<>();
        List<String> teacherEmails = new ArrayList<>();

        for(TeacherResponseDTO teacher:teacherResponses){
            teacherNames.add(teacher.getName());
            teacherEmails.add(teacher.getEmail());
        }
        if (studenCourseRepository.existUserIdAndIdCourse(id, id_course)
        ) {
            StudentCourseEntity studentCourseEntity = studenCourseRepository.findByUserEntityIdAndIdCourse(id, id_course);
            gmailService.buyCourseMail(user.getFullName(),teacherNames,course.getName(),teacherEmails,user.getEmail());
            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
                    .id(studentCourseEntity.getId())
                    .idCourse(id_course)
                    .userEntity(user)
                    .status(StudentCourseStatus.PAID)
                    .isDeleted(false)
                    .build()));
        }else{
            gmailService.buyCourseMail(user.getFullName(),teacherNames,course.getName(),teacherEmails,user.getEmail());
            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
                    .idCourse(id_course)
                    .userEntity(user)
                    .status(StudentCourseStatus.PAID)
                    .isDeleted(false)
                    .build()));
        }
    }
}
