package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.TeacherCalendar2Service;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/teacher")
public class TeacherCalendarController {

    TeacherCalendar2Service teacherCalendarService;

    @GetMapping("/calendar/")
    public List<TeacherCalendarResponseDTO2> getSchedule(@RequestParam("StartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                         @RequestParam("EndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return teacherCalendarService.findByDateRange(startDate, endDate);
    }

    @PostMapping("/{id}/calendar")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('MENTOR')")
    public ResponseData<?> addTeacherCalendar(@RequestBody @Valid TeacherCalendarRequestDTO2 request, @PathVariable(name = "id") UUID id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(teacherCalendarService.createCalendar(request, id))
                .build();
    }

    @PutMapping("/{id}/calendar/{calendarId}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('MENTOR')")
    public ResponseData<?> updateTeacherCalendar(@PathVariable(name = "calendarId") Integer calendarId,
                                                 @RequestBody @Valid TeacherCalendarRequestDTO2 request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.UPDATE_SUCCESSFUL.getCode())
                .message(ErrorCode.UPDATE_SUCCESSFUL.getMessage())
                .result(teacherCalendarService.updateCalendarTeacher(calendarId, request))
                .build();
    }

    @GetMapping("/{id}/calendar/")
    public List<TeacherCalendarResponseDTO2> getSchedule(@RequestParam("StartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                         @RequestParam("EndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                         @PathVariable UUID id) {

        return teacherCalendarService.findByDateRange(startDate, endDate, id);
    }

    @DeleteMapping("/{id}/calendar/{calendarId}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('MENTOR')")
    public ResponseData<?> deleteTeacherCalendar(@PathVariable(name = "calendarId") Integer calendarId,
                                                 @PathVariable(name = "id") UUID ownerId) {
        teacherCalendarService.deleteTeacherCalendar(calendarId, ownerId);

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result("TeacherCalendar with ID " + calendarId + " was successfully deleted.")
                .build();
    }

    @GetMapping("/find-calendars")
    public ResponseData<List<TeacherCalendarResponseDTO2>> findCalendars(
            @RequestParam UUID uuid,
            @RequestParam String technicalTeacherName,
            @RequestParam String chapterName
    ) {
        List<TeacherCalendarResponseDTO2> calendars = teacherCalendarService.findCalendarByTeacherId(uuid, technicalTeacherName, chapterName);
        return ResponseData.<List<TeacherCalendarResponseDTO2>>builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(calendars)
                .build();
    }

    @GetMapping("/calendar/{idCourse}/chapter/{idChapter}/")
    public List<TeacherCalendarResponseDTO2> getCourseChapterTeacherMentor(
            @PathVariable Long idCourse,
            @PathVariable Long idChapter,
            @RequestParam("StartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("EndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return teacherCalendarService.findCourseChapterTeacherMentor(idCourse, idChapter, startDate, endDate);
    }
}
