package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.Mentor;
import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.entity.TeacherCalendar;
import com.techzen.techlearn.entity.UserEntity;
import com.techzen.techlearn.enums.CalendarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherCalendarRepository extends JpaRepository<TeacherCalendar, Integer> {

    boolean existsByTeacherAndStartTimeAndEndTime(Teacher teacher, LocalDateTime startTime, LocalDateTime endTime);
    @Query("select tc from TeacherCalendar tc where tc.startTime >= current_time and tc.teacher.id = :id")
    List<TeacherCalendar> findByTeacherId(UUID id);

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndTeacher(LocalDateTime startTime, LocalDateTime endTime, Teacher teacher);

    @Query("SELECT tc FROM TeacherCalendar tc "
            + "JOIN tc.teacher t ON tc.teacher.id = t.id "
            + "JOIN TeacherCourse tcourse ON t.id = tcourse.teacher.id "
            + "JOIN CourseEntity c ON tcourse.course.id = c.id "
            + "JOIN ChapterEntity ch ON c.id = ch.course.id "
            + "WHERE t.id = :uuid AND tc.status = 'FREE' "
            + "AND c.name LIKE %:courseName% "
            + "AND ch.name LIKE %:chapterName%")
    List<TeacherCalendar> findByFilters(
            @Param("uuid") UUID uuid,
            @Param("courseName") String courseName,
            @Param("chapterName") String chapterName
    );

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndMentor(LocalDateTime startTime, LocalDateTime endTime, Mentor mentor);
    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndUserAndStatus(LocalDateTime startTime, LocalDateTime endTime, UserEntity user, CalendarStatus status);

    Optional<TeacherCalendar> findByIdAndTeacher(Integer id, Teacher teacher);

    Optional<TeacherCalendar> findByIdAndMentor(Integer id, Mentor mentor);

}
