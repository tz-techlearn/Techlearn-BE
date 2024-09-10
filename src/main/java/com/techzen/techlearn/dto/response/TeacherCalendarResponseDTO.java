package com.techzen.techlearn.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherCalendarResponseDTO implements Serializable {
    String idTeacher;
    String dateAppointment;
    String timeStart;
    String timeEnd;
    String status;
    String note;
}
