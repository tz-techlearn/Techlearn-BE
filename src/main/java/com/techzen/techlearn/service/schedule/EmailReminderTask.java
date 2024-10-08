package com.techzen.techlearn.service.schedule;

import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.service.MailService;
import com.techzen.techlearn.service.TeacherCalendar2Service;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailReminderTask {

    MailService mailService;

    TeacherCalendar2Service teacherCalendarService;

    ConcurrentHashMap<Integer, LocalDateTime> reminderCache = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 300000)
    public void sendReminderEmails() throws MessagingException {

        LocalDateTime now = LocalDateTime.now();

        List<TeacherCalendar> upcomingEvents = teacherCalendarService.getEventsBetween(now.plusMinutes(5), now.plusMinutes(10));

        for (TeacherCalendar event : upcomingEvents) {
            if (shouldSendReminder(event.getId(), now)) {
                mailService.sendReminder(event);

                reminderCache.put(event.getId(), now);
            }
        }
    }

    private boolean shouldSendReminder(Integer eventId, LocalDateTime now) {
        if (!reminderCache.containsKey(eventId)) {
            return true;
        }

        LocalDateTime lastSentTime = reminderCache.get(eventId);

        return lastSentTime.isBefore(now.minusMinutes(10));
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanCache() {
        LocalDateTime now = LocalDateTime.now();
        reminderCache.entrySet().removeIf(entry -> entry.getValue().isBefore(now.minusMinutes(10)));
    }
}
