package com.example.smartnotesbackend.repository;

import com.example.smartnotesbackend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    List<Note> findByUserId(Long userId);
    
    List<Note> findByUserIdAndCategoryId(Long userId, Long categoryId);
    
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.isPinned = true ORDER BY n.updatedAt DESC")
    List<Note> findPinnedNotes(@Param("userId") Long userId);
}