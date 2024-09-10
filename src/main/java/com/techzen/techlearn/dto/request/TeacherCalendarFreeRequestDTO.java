package com.techzen.techlearn.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherCalendarFreeRequestDTO {
    String name;
    String timeStart;
    String timeEnd;
    String dateAppointment;
    String isAllDay;
}
