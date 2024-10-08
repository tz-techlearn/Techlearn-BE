package com.techzen.techlearn.controller;

import com.techzen.techlearn.client.CourseClient;
import com.techzen.techlearn.client.TeacherClient;
import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    TeacherService teacherService;

    @GetMapping
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('MENTOR') or hasAuthority('USER')")
    public ResponseData<?> findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                   @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(teacherService.findAll(page, pageSize))
                .build();
    }

    @GetMapping("/")
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('MENTOR') or hasAuthority('USER')")
    public List<TeacherResponseDTO> findAll() {
        return teacherService.findAll();
    }

    @PostMapping
    public ResponseData<?> add(@RequestBody @Valid TeacherRequestDTO request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(teacherService.addTeacher(request))
                .build();
    }

    @GetMapping("/course/{id}")
    public ResponseData<?> filterTeacherByCourse(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(teacherService.filterTeacherByCourse(id))
                .build();
    }

    @PostMapping("/{teacherId}/courses/{courseId}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseData<?> addTeacherToCourse(@PathVariable UUID teacherId, @PathVariable Long courseId) {
        teacherService.addTeacherToCourse(teacherId, courseId);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message("Teacher added to course successfully.")
                .build();
    }

    @GetMapping("/courses/{idCourse}")
    ResponseData<?> getTeacherByIdCourse(@PathVariable Long idCourse){
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .result(teacherService.getTeacherByCourseId(idCourse))
                .build();
    }
}
