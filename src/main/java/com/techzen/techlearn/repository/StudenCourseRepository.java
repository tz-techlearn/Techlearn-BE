package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.StudentCourseEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudenCourseRepository extends JpaRepository<StudentCourseEntity, Long> {
    @Query("SELECT s.idCourse FROM StudentCourseEntity s WHERE s.userEntity.id = :userId")
    List<Long> findAllCourseIdsByUserId(@Param("userId") UUID userId);
}
