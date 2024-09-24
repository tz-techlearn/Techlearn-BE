package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.Mentor;
import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.entity.TeacherCalendar;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherCalendarMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "status")
    TeacherCalendar toEntity(TeacherCalendarRequestDTO2 request);

    @Mapping(target = "ownerId", expression = "java(mapOwnerId(teacherCalendar.getTeacher(), teacherCalendar.getMentor()))")
    @Mapping(target = "userId", source = "user.id")
    TeacherCalendarResponseDTO2 toDTO(TeacherCalendar teacherCalendar);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TeacherCalendarRequestDTO2 request, @MappingTarget TeacherCalendar entity);

    default String mapOwnerId(Teacher teacher, Mentor mentor) {
        if (teacher != null) {
            return teacher.getId().toString();
        } else if (mentor != null) {
            return mentor.getId().toString();
        }
        return null;
    }
}


