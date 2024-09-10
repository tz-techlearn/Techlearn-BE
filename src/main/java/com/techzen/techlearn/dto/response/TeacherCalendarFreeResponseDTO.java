package com.techzen.techlearn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherCalendarFreeResponseDTO {
    String name;
    String timeStart;
    String timeEnd;
    String dateAppointment;
    String isAllDay;
}
