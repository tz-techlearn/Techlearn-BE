package com.techzen.techlearn.service.impl;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.TeacherCalendarMapper;
import com.techzen.techlearn.repository.TeacherCalendarRepository;
import com.techzen.techlearn.repository.TeacherRepository;
import com.techzen.techlearn.service.TeacherCalendar2Service;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherCalendarServiceImpl implements TeacherCalendar2Service {

    TeacherCalendarRepository teacherCalendarRepository;
    TeacherCalendarMapper teacherCalendarMapper;
    TeacherRepository teacherRepository;

    @Override
    public List<TeacherCalendarResponseDTO2> addTeacherCalendar(TeacherCalendarRequestDTO2 request) {

        UUID teacherId = UUID.fromString(request.getOwnerId());
        LocalDateTime timeStart = LocalDateTime.parse(request.getStartTime());
        LocalDateTime timeEnd = LocalDateTime.parse(request.getEndTime());

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));

        if (timeStart.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.DATE_APPOINTMENT_NOT_SUITABLE);
        }

        List<TeacherCalendar> savedCalendars = new ArrayList<>();
        LocalDateTime intervalStart = timeStart;

        while (intervalStart.isBefore(timeEnd)) {
            LocalDateTime intervalEnd = intervalStart.plusMinutes(10);

            if (intervalEnd.isAfter(timeEnd)) {
                intervalEnd = timeEnd;
            }

            if (teacherCalendarRepository.existsByTeacherAndStartTimeAndEndTime(teacher, intervalStart, intervalEnd)) {
                throw new AppException(ErrorCode.TEACHER_CALENDAR_DATE_APPOINTMENT_EXISTED);
            }

            TeacherCalendar entity = teacherCalendarMapper.toEntity(request);
            entity.setStartTime(intervalStart);
            entity.setEndTime(intervalEnd);
            entity.setTeacher(teacher);

            TeacherCalendar savedEntity = teacherCalendarRepository.save(entity);
            savedCalendars.add(savedEntity);

            intervalStart = intervalEnd;
        }

        return savedCalendars.stream()
                .map(teacherCalendarMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<TeacherCalendarResponseDTO2> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<TeacherCalendar> entities = teacherCalendarRepository.findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(startDate, endDate);
        return entities.stream().map(teacherCalendarMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public void deleteTeacherCalendar(Integer id) {
        TeacherCalendar calendar = teacherCalendarRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CALENDAR_NOT_EXISTED));

        teacherCalendarRepository.delete(calendar);
    }

    @Override
    public TeacherCalendarResponseDTO2 updateCalendarTeacher(Integer id, TeacherCalendarRequestDTO2 request) {
        TeacherCalendar entity = teacherCalendarRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CALENDAR_NOT_EXISTED));

        UUID teacherId = UUID.fromString(request.getOwnerId());
        LocalDateTime timeStart = LocalDateTime.parse(request.getStartTime());
        LocalDateTime timeEnd = LocalDateTime.parse(request.getEndTime());

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));

        TeacherCalendar calendar = teacherCalendarMapper.toEntity(request);

        calendar.setId(id);
        calendar.setTeacher(teacher);
        calendar.setStartTime(timeStart);
        calendar.setEndTime(timeEnd);
        TeacherCalendar savedEntity = teacherCalendarRepository.save(calendar);

        return teacherCalendarMapper.toDTO(savedEntity);
    }

//    @Override
//    public List<TeacherCalendarResponseDTO2> findCalendarByTeacherId(String id) {
//        UUID teacherId = UUID.fromString(id);
//      teacherRepository.findById(teacherId).orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_EXISTED));
//        List<TeacherCalendar> calendars = teacherCalendarRepository.findByTeacherId(teacherId);
//        return calendars.stream()
//                .map(teacherCalendarMapper::toDTO)
//                .collect(Collectors.toList());
//    }

//    @Override
//    public List<TeacherCalendarResponseDTO2> findCalendarByTeacherId(
//            String teacherName,
//            String technicalTeacherName,
//            String chapterName
//    ) {
//        List<TeacherCalendar> calendars = teacherCalendarRepository.findByFilters(
//                teacherName, technicalTeacherName, chapterName
//        );
//        System.out.println(calendars);
//        return calendars.stream()
//                .map(teacherCalendarMapper::toDTO)
//                .collect(Collectors.toList());
//    }

}
