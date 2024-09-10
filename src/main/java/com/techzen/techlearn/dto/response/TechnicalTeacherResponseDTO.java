package com.techzen.techlearn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalTeacherResponseDTO {
    String name;
    String timeStart;
    String timeEnd;
    String dateAppointment;
}
