package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.Category;
import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.entity.User;
import com.example.smartnotesbackend.repository.CategoryRepository;
import com.example.smartnotesbackend.repository.NoteRepository;
import com.example.smartnotesbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public NoteService(NoteRepository noteRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository) {

        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public Note createNote(Long userId, Note note) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        note.setUser(user);

        if (note.getCategory() != null &&
                note.getCategory().getId() != null) {

            Category category = categoryRepository
                    .findByIdAndUserId(note.getCategory().getId(), userId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            note.setCategory(category);
        } else {
            note.setCategory(null);
        }

        return noteRepository.save(note);
    }

    // READ
    public List<Note> getNotesByUser(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    // UPDATE
    public Note updateNote(Long id, Note updatedNote) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());

        if (updatedNote.getIsPinned() != null) {
            note.setIsPinned(updatedNote.getIsPinned());
        }

        if (updatedNote.getCategory() != null &&
                updatedNote.getCategory().getId() != null) {

            Category category = categoryRepository.findById(
                    updatedNote.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            note.setCategory(category);
        }

        return noteRepository.save(note);
    }

    // DELETE
    public void deleteNote(Long id) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        noteRepository.delete(note);
    }
}