package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.TeacherCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentCalendarRepository extends JpaRepository<TeacherCalendar, Integer> {
    @Query("SELECT tc FROM TeacherCalendar tc " +
            "JOIN tc.teacher t " +
            "JOIN tc.user u " +
            "WHERE u.id = :userId and tc.status = BOOKED")
    List<TeacherCalendar> findAllByUserCalendar(@Param("userId") UUID id);

    @Modifying
    @Query("UPDATE TeacherCalendar tc SET tc.status = 'FREE', tc.user.id = NULL WHERE tc.id = :id")
    void cancelByUserIdCalendar(@Param("id") UUID id);

    @Query("SELECT tc FROM TeacherCalendar tc WHERE tc.id = :id")
    Optional<TeacherCalendar> findIdUserCalendar(@Param("id") UUID id);

}
