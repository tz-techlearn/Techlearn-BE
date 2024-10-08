package com.techzen.techlearn.dto.response;


import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.StudentCourseStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterTrialDTO {
    Long id;
    Long idCourse;
    UUID idUser;
    Boolean isDeleted;
    StudentCourseStatus status;
}
