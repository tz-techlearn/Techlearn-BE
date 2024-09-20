package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.TeacherCalendarRequestDTO2;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherCalendarResponseDTO2;
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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/teacher-calendar")
public class TeacherCalendarController {

    TeacherCalendar2Service teacherCalendarService;

    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseData<?> addTeacherCalendar(@RequestBody @Valid TeacherCalendarRequestDTO2 request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(teacherCalendarService.addTeacherCalendar(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseData<?> updateTeacherCalendar(@PathVariable Integer id,
                                                 @RequestBody @Valid TeacherCalendarRequestDTO2 request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.UPDATE_SUCCESSFUL.getCode())
                .message(ErrorCode.UPDATE_SUCCESSFUL.getMessage())
                .result(teacherCalendarService.updateCalendarTeacher(id, request))
                .build();
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('TEACHER')")
    public List<TeacherCalendarResponseDTO2> getSchedule(@RequestParam("StartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                         @RequestParam("EndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return teacherCalendarService.findByDateRange(startDate, endDate);
    }
//
//    @GetMapping("/students")
//    public ResponseData<?> getAppointmentsByTechnicalAndTeacher(@RequestParam String nameTechnical,
//                                                                @RequestParam String nameTeacher) {
//        return ResponseData.builder()
//                .status(HttpStatus.OK.value())
//                .code(ErrorCode.GET_SUCCESSFUL.getCode())
//                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
//                .result(teacherCalendarService.findAppointments(nameTechnical, nameTeacher))
//                .build();
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseData<?> deleteTeacherCalendar(@PathVariable Integer id) {
        teacherCalendarService.deleteTeacherCalendar(id);

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result("TeacherCalendar with ID " + id + " was successfully deleted.")
                .build();
    }

//    @GetMapping("/find-by-id/{id}")
//    public List<TeacherCalendarResponseDTO2> findTeacherCalendarById(@PathVariable String id) {
//        return teacherCalendarService.findCalendarByTeacherId(id);
//    }

//    @GetMapping("/find-calendars")
//    public ResponseData<List<TeacherCalendarResponseDTO2>> findCalendars(
//            @RequestParam(required = false) String teacherName,
//            @RequestParam(required = false) String technicalTeacherName,
//            @RequestParam(required = false) String chapterName
//    ) {
//        List<TeacherCalendarResponseDTO2> calendars = teacherCalendarService.findCalendarByTeacherId(teacherName, technicalTeacherName, chapterName);
//
//        return ResponseData.<List<TeacherCalendarResponseDTO2>>builder()
//                .status(HttpStatus.OK.value())
//                .code(ErrorCode.GET_SUCCESSFUL.getCode())
//                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
//                .result(calendars)
//                .build();
//    }

}
