package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO;
import com.techzen.techlearn.dto.response.TeacherCalendarFreeResponseDTO;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO;

import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.dto.response.TechnicalTeacherResponseDTO;
import com.techzen.techlearn.entity.CalendarEntity;
import com.techzen.techlearn.entity.TeacherCalendarEntity;
import com.techzen.techlearn.entity.TeacherEntity;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.ITeacherCalendarMapperFree;
import com.techzen.techlearn.mapper.TeacherCalendarMappingContext;
import com.techzen.techlearn.mapper.TeacherCalendarMapper;
import com.techzen.techlearn.mapper.TechnicalTeacherMapper;
import com.techzen.techlearn.repository.CalendarRepository;
import com.techzen.techlearn.repository.TeacherCalendarRepository;
import com.techzen.techlearn.repository.TeacherRepository;
import com.techzen.techlearn.service.TeacherCalendarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherCalendarServiceImpl implements TeacherCalendarService {
    TeacherCalendarRepository teacherCalendarRepository;
    TeacherCalendarMapper teacherCalendarMapper;
    TeacherCalendarMappingContext teacherCalendarMappingContext;
    TeacherRepository teacherRepository;
    ITeacherCalendarMapperFree iTeacherCalendarMapperFree;
    TechnicalTeacherMapper technicalTeacherMapper;

    @Override
    public TeacherCalendarResponseDTO addTeacherCalendar(TeacherCalendarRequestDTO request) {
        UUID teacherId = UUID.fromString(request.getIdTeacher());
        LocalDate dateAppointment = LocalDate.parse(request.getDateAppointment());
        LocalTime timeStart = LocalTime.parse(request.getTimeStart());
        LocalTime timeEnd = LocalTime.parse(request.getTimeEnd());

        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
        System.out.println(teacher);
        if (teacherCalendarRepository.existsByTeacherAndDateAppointmentAndTimeStartAndTimeEnd(teacher,dateAppointment,timeStart,timeEnd)) {
            throw new AppException(ErrorCode.TEACHER_CALENDAR_DATE_APPOINTMENT_EXISTED);
        }

        if (dateAppointment.isBefore(LocalDate.now()))
            throw new AppException(ErrorCode.DATE_APPOINTMENT_NOT_SUITABLE);

//        if (timeStart.isBefore(LocalTime.now())) {
//            throw new AppException(ErrorCode.TIME_START_SUITABLE);
//        }else if (timeStart.until(timeEnd, ChronoUnit.MINUTES) != 10){
//            throw new AppException(ErrorCode.TIME_NOT_SUITABLE);
//        }

        TeacherCalendarEntity entity = teacherCalendarMapper.toTeacherCalendarEntity(request, teacherCalendarMappingContext);
        entity.setTeacher(teacher);
        entity.setDateAppointment(dateAppointment);
        TeacherCalendarEntity savedEntity = teacherCalendarRepository.save(entity);
        return teacherCalendarMapper.toTeacherCalendarResponseDTO(savedEntity);
    }

    @Override
    public List<TeacherCalendarFreeResponseDTO> getAllTeacherCalendar() {
        List<Object[]> resultList = teacherCalendarRepository.findAllTeacherFree();
        System.out.println(resultList);
        return resultList.stream()
                .map(iTeacherCalendarMapperFree::toTeacherCalendarFreeResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TechnicalTeacherResponseDTO> findAppointments(String technicalName, String teacherName) {
        List<TeacherCalendarEntity> teacherCalendars = teacherCalendarRepository.findAppointmentsByTechnicalAndTeacher(technicalName, teacherName);
        if (teacherCalendars.isEmpty())
            throw new AppException(ErrorCode.NAME_TEACHER_OR_TECHNICAL_AND_CURRENT_DATE_NOT_EXISTED);
        return teacherCalendars.stream()
                .map(technicalTeacherMapper.INSTANCE::toTechnicalTeacherResponseDTO)
                .collect(Collectors.toList());
    }
}
