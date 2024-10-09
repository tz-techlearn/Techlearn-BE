package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.response.RegisterTrialDTO;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface RegisterTrialService {
    public RegisterTrialDTO save(UUID idUser, long idCourse) throws MessagingException;
}
