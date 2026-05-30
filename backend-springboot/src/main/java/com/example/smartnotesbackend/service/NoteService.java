package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.entity.User;
import com.example.smartnotesbackend.repository.NoteRepository;
import com.example.smartnotesbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository,
                       UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    public Note createNote(Long userId, Note note) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        note.setUser(user);

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

        return noteRepository.save(note);
    }

    // DELETE
    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }
}