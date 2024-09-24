package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TeacherCalendar2Service {
    TeacherCalendarResponseDTO2 createCalendar(TeacherCalendarRequestDTO2 request, UUID id);

    List<TeacherCalendarResponseDTO2> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, UUID id);

    void deleteTeacherCalendar(Integer id, UUID ownerId);

    TeacherCalendarResponseDTO2 updateCalendarTeacher(Integer id, TeacherCalendarRequestDTO2 request);

    List<TeacherCalendarResponseDTO2> findCalendarByTeacherId(UUID uuid, String technicalTeacherName, String chapterName);

}
