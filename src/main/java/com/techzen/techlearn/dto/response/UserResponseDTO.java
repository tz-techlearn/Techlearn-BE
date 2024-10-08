package com.techzen.techlearn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techzen.techlearn.entity.ChapterEntity;
import com.techzen.techlearn.entity.CourseEntity;
import com.techzen.techlearn.entity.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO implements Serializable {
    UUID id;
    String fullName;
    Integer age;
    String email;
    Integer points;
    String avatar;
    @JsonProperty("isMentor")
    boolean isMentor;
    @JsonProperty("isTeacher")
    boolean isTeacher;
    Set<Role> roles;
    List<ChapterEntity> chapters;
    List<CourseEntity> courses;

}
