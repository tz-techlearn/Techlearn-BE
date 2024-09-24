package com.techzen.techlearn.service;

import com.techzen.techlearn.dto.request.MentorRequestDTO;
import com.techzen.techlearn.dto.response.MentorResponseDTO;

import java.util.List;

public interface MentorService {
    MentorResponseDTO createMentor(MentorRequestDTO dto);

    List<MentorResponseDTO> findAllMentor();

    List<MentorResponseDTO> filterMentorByChapter(Long id);
}
