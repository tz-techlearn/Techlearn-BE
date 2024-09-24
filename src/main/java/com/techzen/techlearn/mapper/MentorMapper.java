package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.request.MentorRequestDTO;
import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.MentorResponseDTO;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.entity.Mentor;
import com.techzen.techlearn.entity.Teacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MentorMapper {

    Mentor toEntity(MentorRequestDTO dto);

    MentorResponseDTO toTeacherResponseDTO(Mentor entity);
}
