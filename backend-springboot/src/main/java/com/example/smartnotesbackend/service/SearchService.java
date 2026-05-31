package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.dto.NoteDTO;
import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.repository.NoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final NoteRepository noteRepository;

    public SearchService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }


    public List<NoteDTO> searchNotes(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Trả list rỗng thay vì toàn bộ notes — nhất quán với logic frontend
            return List.of();
        }
        return noteRepository.searchByKeyword(userId, keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<NoteDTO> getPinnedNotes(Long userId) {
        return noteRepository.findPinnedNotes(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteDTO> getNotesByCategory(Long userId, Long categoryId) {
        return noteRepository.findByUserIdAndCategoryId(userId, categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NoteDTO convertToDTO(Note note) {
        Long categoryId = null;
        String categoryName = null;
        String categoryColorCode = null;

        if (note.getCategory() != null) {
            categoryId = note.getCategory().getId();
            categoryName = note.getCategory().getName();
            categoryColorCode = note.getCategory().getColorCode();
        }

        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getUserId(),
                categoryId,
                categoryName,
                categoryColorCode,
                note.getCreatedAt(),
                note.getUpdatedAt(),
                note.getIsPinned()
        );
    }
}