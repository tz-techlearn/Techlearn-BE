package com.techzen.techlearn.mapper;

import com.techzen.techlearn.dto.response.TechnicalTeacherResponseDTO;
import com.techzen.techlearn.entity.TeacherCalendarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TechnicalTeacherMapper {

    TechnicalTeacherMapper INSTANCE = Mappers.getMapper(TechnicalTeacherMapper.class);

    @Mapping(source = "teacher.name", target = "name")
    @Mapping(source = "calendar.timeStart", target = "timeStart")
    @Mapping(source = "calendar.timeEnd", target = "timeEnd")
    @Mapping(source = "teacherCalendar.dateAppointment", target = "dateAppointment")
    TechnicalTeacherResponseDTO toTechnicalTeacherResponseDTO(TeacherCalendarEntity teacherCalendar);
}
