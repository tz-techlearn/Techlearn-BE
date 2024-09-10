package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO;
import com.techzen.techlearn.dto.response.TechnicalTeacherResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeacherCalendarService {
    TeacherCalendarResponseDTO addTeacherCalendar(TeacherCalendarRequestDTO request);
    List<?> getAllTeacherCalendar();

    List<TechnicalTeacherResponseDTO> findAppointments(String technicalName, String teacherName);
}
