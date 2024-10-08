package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.StudentCalendarService;
import com.techzen.techlearn.service.TeacherCalendar2Service;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/student")
public class StudentCalendarController {

    StudentCalendarService studentCalendarService;
    TeacherCalendar2Service teacherCalendarService;

    @GetMapping("/{id}/calendar/")
    public List<TeacherCalendarResponseDTO2> getSchedule(@RequestParam("StartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                         @RequestParam("EndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                         @PathVariable UUID id) {

        return teacherCalendarService.findByDateRange(startDate, endDate, id);
    }

    @PostMapping("/{id}/calendar")
    public ResponseData<?> addStudentCalendar(@RequestBody @Valid TeacherCalendarRequestDTO2 request,
                                              @PathVariable UUID id) throws MessagingException, IOException, GeneralSecurityException {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(studentCalendarService.addStudentCalendar(request))
                .build();
    }

    @DeleteMapping("/{id}/calendar/{calendarId}")
    public ResponseData<?> cancelStudentCalendar(@PathVariable(name = "calendarId") Integer calendarId,
                                                 @PathVariable(name = "id") UUID id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.UPDATE_SUCCESSFUL.getCode())
                .message(ErrorCode.UPDATE_SUCCESSFUL.getMessage())
                .result(studentCalendarService.cancelCalendarStudentById(calendarId))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseData<?> findAllCalendarStudent(@PathVariable UUID id){
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(studentCalendarService.getStudentCalendarsByUserId(id))
                .build();
    }
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('USER')")
    @PostMapping("/cancelBooking/{idBooking}")
    public ResponseData<?> cancelBooking (@PathVariable Integer idBooking) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(studentCalendarService.cancelBooking(idBooking))
                .build();
    }
}
