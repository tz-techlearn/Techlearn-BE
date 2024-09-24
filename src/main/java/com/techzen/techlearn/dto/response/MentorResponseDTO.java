package com.techzen.techlearn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorResponseDTO {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String color;
}