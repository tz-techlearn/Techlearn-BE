package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
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
}
