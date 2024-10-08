package com.techzen.techlearn.controller;

import com.techzen.techlearn.dto.response.ResponseData;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.service.RegisterTrialService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequestMapping("/api/v1/register_trials")
public class RegisterTrialController {

    RegisterTrialService registerTrialService;

    @PostMapping
    public ResponseData<?> postRegisterTrial( @RequestParam UUID idUser, @RequestParam long idCourse) throws MessagingException {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .code(ErrorCode.GET_SUCCESSFUL.getCode())
                .message(ErrorCode.GET_SUCCESSFUL.getMessage())
                .result(registerTrialService.save(idUser, idCourse))
                .build();
    }

}
