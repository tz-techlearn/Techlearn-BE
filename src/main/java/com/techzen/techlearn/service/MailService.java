package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.CalendarDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface MailService {
    void sendScheduleSuccessEmail(CalendarDTO calenderDto) throws MessagingException, IOException;

    void sendEmails(List<String> recipientEmails, String subject, String title, String description,
                    LocalDateTime startTime, LocalDateTime endTime, String actionUrl, String actionText, String primaryColor) throws MessagingException;

}