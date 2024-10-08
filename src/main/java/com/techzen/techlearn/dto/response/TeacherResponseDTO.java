package com.techzen.techlearn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponseDTO {

    @JsonProperty("Id")
    private UUID id;

    @JsonProperty("OwnerText")
    private String name;

    private String avatar;

    @JsonProperty("OwnerColor")
    private String color;

    private String email;
}
