package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO;
import com.techzen.techlearn.entity.TeacherCalendarEntity;
import com.techzen.techlearn.entity.TeacherEntity;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TeacherCalendarMapper {

    @Mappings({
            @Mapping(target = "teacher", source = "idTeacher", qualifiedByName = "mapToTeacherEntity")
    })
    TeacherCalendarEntity toTeacherCalendarEntity(TeacherCalendarRequestDTO dto, @Context TeacherCalendarMappingContext context);

    @Mappings({
            @Mapping(target = "idTeacher", source = "teacher.id"),
    })
    TeacherCalendarResponseDTO toTeacherCalendarResponseDTO(TeacherCalendarEntity entity);

    @Named("mapToTeacherEntity")
    default TeacherEntity mapToTeacherEntity(String idTeacher, @Context TeacherCalendarMappingContext context) {
        return context.getTeacherRepository().findById(UUID.fromString(idTeacher))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

}
