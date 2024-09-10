package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.TeacherCalendarEntity;
import com.techzen.techlearn.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TeacherCalendarRepository extends JpaRepository<TeacherCalendarEntity, Integer> {

    boolean existsByTeacherAndDateAppointmentAndTimeStartAndTimeEnd(TeacherEntity teacher, LocalDate dateAppointment, LocalTime timeStart, LocalTime timeEnd);

    @Query(nativeQuery = true, value = "SELECT \n" +
            "    teacher.name, \n" +
            "    teacher_calendar.time_start, \n" +
            "    teacher_calendar.time_end, \n" +
            "    teacher_calendar.date_appointment,\n" +
            "    teacher_calendar.is_all_day\n" +
            "from teacher_calendar\n" +
            "JOIN \n" +
            "    teacher ON teacher_calendar.id_teacher = teacher.id \n" +
            "WHERE \n" +
            "    teacher_calendar.status = 'Đang trống' \n" +
            "    AND (\n" +
            "        teacher_calendar.date_appointment > CURRENT_DATE \n" +
            "        OR (teacher_calendar.date_appointment = CURRENT_DATE AND teacher_calendar.time_start > CURRENT_TIME)\n" +
            "    );\n")
    List<Object[]> findAllTeacherFree();
   // Page<Object[]> findAllTeacherFree(Pageable pageable);

}
