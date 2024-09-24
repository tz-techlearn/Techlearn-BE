package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {

    @Query("SELECT m FROM Mentor m JOIN m.chapters c WHERE c.id = :id")
    List<Mentor> findMentorByChapter(@Param("id") Long id);
}
