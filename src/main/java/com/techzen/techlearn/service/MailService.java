package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.CalendarDTO;
import com.techzen.techlearn.dto.response.PointResponseDTO;
import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.entity.UserEntity;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface MailService {
    void sendScheduleSuccessEmail(CalendarDTO calenderDto) throws MessagingException, IOException;
    void sendReminder(TeacherCalendar event) throws MessagingException;
    void sendEmails(List<String> recipientEmails, String subject, String title, String actionUrl, String actionText, String primaryColor, TeacherCalendar calendar) throws MessagingException;

    void sendMailSupportPoints(PointResponseDTO point, UserResponseDTO user) throws MessagingException;
}