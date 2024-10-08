package com.techzen.techlearn.repository;

import com.techzen.techlearn.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {

    @Query("SELECT m FROM Mentor m JOIN m.chapters c WHERE c.id = :id")
    List<Mentor> findMentorByChapter(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM mentor_chapter WHERE mentor_id = :uuid AND chapter_id = :id", nativeQuery = true)
    void deleteMentorChapter(@Param("uuid") UUID uuid, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mentor_chapter SET chapter_id = :newChapterId WHERE chapter_id = :oldChapterId", nativeQuery = true)
    void updateMentorChapter(@Param("oldChapterId") Long oldChapterId, @Param("newChapterId") Long newChapterId);

    Optional<Mentor> findByEmail(String email);
}
