package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherCalendar2Service {
    List<TeacherCalendarResponseDTO2> addTeacherCalendar(TeacherCalendarRequestDTO2 request);

    List<TeacherCalendarResponseDTO2> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void deleteTeacherCalendar(Integer id);

    TeacherCalendarResponseDTO2 updateCalendarTeacher(Integer id, TeacherCalendarRequestDTO2 request);

//    List<TeacherCalendarResponseDTO2> findCalendarByTeacherId(String teacherName, String technicalTeacherName, String chapterName);

}
