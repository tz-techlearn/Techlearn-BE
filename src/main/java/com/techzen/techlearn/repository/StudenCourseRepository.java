package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.StudentCourseEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudenCourseRepository extends JpaRepository<StudentCourseEntity, Long> {

    @Query("SELECT s.idCourse FROM StudentCourseEntity s WHERE s.userEntity.id = :userId")
    List<Long> findAllCourseIdsByUserId(@Param("userId") UUID userId);

    @Query("SELECT (COUNT(s) > 0) AS BIT FROM StudentCourseEntity s WHERE s.userEntity.id = :userId and s.idCourse= :idCourse  ")
    boolean existUserIdAndIdCourse(@Param("userId") UUID userId, @Param("idCourse") long idCourse);

    @Query("SELECT s from StudentCourseEntity s WHERE s.userEntity.id = :userId and s.idCourse= :idCourse")
    StudentCourseEntity findStudentCourseByIdCourseIdUser(@Param("userId") UUID userId, @Param("idCourse") long idCourse);

    List<StudentCourseEntity> findAllByUserEntityId(@Param("id") UUID id);

}
