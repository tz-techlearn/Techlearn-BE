package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.MentorRequestDTO;
import com.techzen.techlearn.dto.request.TeacherRequestDTO;
import com.techzen.techlearn.dto.response.MentorResponseDTO;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.TeacherResponseDTO;
import com.techzen.techlearn.entity.Mentor;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.MentorService;
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
@RequestMapping("/api/v1/mentors")
public class MentorController {

    MentorService mentorService;

//    @GetMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseData<?> findAll(@RequestParam(required = false, defaultValue = "1") int page,
//                                   @RequestParam(required = false, defaultValue = "10") int pageSize) {
//        return ResponseData.builder()
//                .status(HttpStatus.OK.value())
//                .code(ErrorCode.GET_SUCCESSFUL.getCode())
//                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
//                .result(teacherService.findAll(page, pageSize))
//                .build();
//    }

    @GetMapping("/")
    public List<MentorResponseDTO> findAll() {
        return mentorService.findAllMentor();
    }

    @PostMapping
    public ResponseData<?> add(@RequestBody @Valid MentorRequestDTO request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(mentorService.createMentor(request))
                .build();
    }

    @GetMapping("/chapter/{id}")
    public ResponseData<?> filterMentorByChapter(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(mentorService.filterMentorByChapter(id))
                .build();
    }

}

