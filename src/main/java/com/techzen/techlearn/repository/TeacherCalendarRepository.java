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

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndStatusIn(LocalDateTime startTime, LocalDateTime endTime, List<CalendarStatus> statuses);

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndTeacherAndStatusIn(LocalDateTime startTime, LocalDateTime endTime, Teacher teacher, List<CalendarStatus> statuses);

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

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndMentorAndStatusIn(LocalDateTime startTime, LocalDateTime endTime, Mentor mentor, List<CalendarStatus> statuses);

    List<TeacherCalendar> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndUserAndStatusAndStatusIn(LocalDateTime startTime, LocalDateTime endTime, UserEntity user, CalendarStatus status, List<CalendarStatus> statuses);

    Optional<TeacherCalendar> findByIdAndTeacher(Integer id, Teacher teacher);

    Optional<TeacherCalendar> findByIdAndMentor(Integer id, Mentor mentor);

    @Query("SELECT tc " +
            "FROM CourseEntity c " +
            "JOIN c.listChapter ch " +
            "JOIN c.teachers t " +
            "JOIN t.teacherCalendars tc " +
            "JOIN ch.mentors m " +
            "WHERE c.id = :idCourse " +
            "AND ch.id = :idChapter " +
            "AND tc.startTime >= :startDate " +
            "AND tc.endTime <= :endDate " +
            "AND (tc.status = 'BUSY' OR tc.status = 'BOOKED')" )
    List<TeacherCalendar> findCourseChapterTeacherMentor(@Param("idCourse") Long idCourse,
                                                  @Param("idChapter") Long idChapter,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    List<TeacherCalendar> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
