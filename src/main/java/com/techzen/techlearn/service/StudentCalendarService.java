package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.TeacherCalendar;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

public interface StudentCalendarService {
    TeacherCalendarResponseDTO2 addStudentCalendar(TeacherCalendarRequestDTO2 request) throws MessagingException, IOException, GeneralSecurityException;

    TeacherCalendarResponseDTO2 cancelCalendarStudentById(Integer id);

    List<TeacherCalendarResponseDTO2> getStudentCalendarsByUserId(UUID id);

    Integer cancelBooking (Integer bookingId);
}
