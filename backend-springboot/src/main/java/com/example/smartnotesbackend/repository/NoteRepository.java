package com.example.smartnotesbackend.repository;

import com.example.smartnotesbackend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserId(Long userId);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)")
    List<Note> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isPinned = true")
    List<Note> findPinnedNotes(@Param("userId") Long userId);

    List<Note> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT n FROM Note n WHERE n.scheduleMode = com.example.smartnotesbackend.entity.ScheduleMode.EVERYDAY OR n.scheduleDate = :date")
    List<Note> findScheduledNotes(@Param("date") LocalDate date);

    @Query("SELECT n FROM Note n WHERE n.nextScheduledAt IS NOT NULL AND n.nextScheduledAt <= :now")
    List<Note> findDueNotes(@Param("now") LocalDateTime now);
}