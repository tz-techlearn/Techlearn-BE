package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.request.RoleRequest;
import com.techzen.techlearn.dto.request.UserRequestDTO;
import com.techzen.techlearn.dto.response.PageResponse;
import com.techzen.techlearn.dto.response.PointResponseDTO;
import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.dto.response.UserResponseDTO;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    UserService userService;

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
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or (hasAuthority('USER') and #id == principal.id)")
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

    @PostMapping("/{id}/add-roles")
    public ResponseData<?> addRoles(@PathVariable UUID id, @RequestBody RoleRequest roleRequest) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .result(userService.addRole(id, roleRequest.getRoles()))
                .build();
    }

    @PostMapping("/{id}/remove-roles")
    public ResponseData<?> removeRoles(@PathVariable UUID id, @RequestBody RoleRequest roleRequest) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .result(userService.removeRoles(id, roleRequest.getRoles()))
                .build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('USER') or hasAuthority('MENTOR')")
    public ResponseData<?> retrieveUser() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.retrieveUser())
                .build();
    }
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('USER') or hasAuthority('MENTOR')")
    public ResponseData<?> updateUserMe(@RequestBody UserResponseDTO userResponseDTO) {
        UserResponseDTO updatedUser = userService.updateUserMe(userResponseDTO);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.UPDATE_SUCCESSFUL.getCode())
                .message(ErrorCode.UPDATE_SUCCESSFUL.getMessage())
                .result(updatedUser)
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

    @GetMapping("/points-package")
    @PreAuthorize("hasAuthority('USER')")
    public PageResponse<?> findAllPointsPackage (@RequestParam(required = false, defaultValue = "1") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return userService.findAllPointsPackage(page, pageSize);
    }

    @PostMapping("/request-points")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseData<?> requestPointsPurchase (@RequestBody PointResponseDTO dto) throws MessagingException {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(userService.requestPointsPurchase(dto))
                .build();
    }
}
