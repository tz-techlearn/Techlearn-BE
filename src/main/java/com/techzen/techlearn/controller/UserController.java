package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.CalendarDTO;
import com.techzen.techlearn.dto.request.RoleRequest;
import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.MailService;
import com.techzen.techlearn.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    UserService userService;

    MailService mailService;

    @GetMapping
    public ResponseData<?> getAllUser(@RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.getAllUser(page, pageSize))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or (hasAuthority('USER') and #id == principal.id)")
    public ResponseData<?> getUserById(@PathVariable UUID id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping("/access-token")
    public ResponseData<?> getUserByAccessToken(@RequestParam String accessToken) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.getUserEntityByAccessToken(accessToken))
                .build();
    }

    @PostMapping
    public ResponseData<?> addUser(@RequestBody @Valid UserRequestDTO request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.ADD_SUCCESSFUL.getCode())
                .message(ErrorCode.ADD_SUCCESSFUL.getMessage())
                .result(userService.addUser(request))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable UUID id, @RequestBody UserRequestDTO request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.UPDATE_SUCCESSFUL.getCode())
                .message(ErrorCode.UPDATE_SUCCESSFUL.getMessage())
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.DELETE_SUCCESSFUL.getCode())
                .message(ErrorCode.DELETE_SUCCESSFUL.getMessage())
                .message("Delete user success")
                .build();
    }

    @GetMapping("/send-email")
    public void sendEmail() {
        try {
            CalendarDTO calendarDTO = CalendarDTO.builder()
                    .attendees(List.of("thanhtuanle0209@gmail.com"))  // Danh sách email người nhận
                    .subject("Thông báo lịch hỗ trợ online 1v1")
                    .description("Chúng tôi xin thông báo rằng bạn đã đặt lịch hỗ trợ 1v1 online thành công. Đây là cơ hội để bạn được hỗ trợ trực tiếp bởi giảng viên/tư vấn viên của chúng tôi, giải đáp mọi thắc mắc và giúp bạn đạt được mục tiêu học tập của mình.") // Mô tả của sự kiện
                    .summary("lịch hỗ trợ online 1v1 ")
                    .meetingLink("https://example.com/meeting")
                    .eventDateTime(LocalDateTime.now().plusMinutes(10)) // (10p sau) // LocalDateTime.of(2024, 09, 05, 14, 30)) Ngày cụ thể: 05/09/2024 lúc 14:30
                    .build();
            mailService.sendScheduleSuccessEmail(calendarDTO);
        } catch (MessagingException | IOException e) {
            log.error("Error while sending email: {}", e.getMessage());
        }
    }

    @PostMapping("/{id}/add-roles")
    public ResponseData<?> addRole(@PathVariable UUID id, @RequestBody RoleRequest roleRequest) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .result(userService.addRole(id, roleRequest.getRoles()))
                .build();
    }

    @GetMapping("/me")
    public ResponseData<?> retrieveUser() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.retrieveUser())
                .build();
    }

    @GetMapping("/points")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseData<?> getAllPointsById (@RequestParam UUID idUser) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.getAllPointsById(idUser))
                .build();
    }
}
