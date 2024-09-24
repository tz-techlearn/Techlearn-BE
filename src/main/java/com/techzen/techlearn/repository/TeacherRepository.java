package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.Teacher;
import com.techzen.techlearn.entity.TeacherCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    @Query("SELECT t FROM Teacher t " +
            "JOIN TeacherCourse tc ON t.id = tc.teacher.id " +
            "JOIN CourseEntity c ON tc.course.id = c.id " +
            "WHERE :id = c.id")
    List<Teacher> findTeacherByCourse(@Param("id") Long id);
}
