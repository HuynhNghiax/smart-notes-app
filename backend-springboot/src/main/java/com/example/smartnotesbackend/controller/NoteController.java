package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin("*")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // CREATE
    @PostMapping("/{userId}")
    public ResponseEntity<Note> createNote(
            @PathVariable Long userId,
            @Valid @RequestBody Note note) {

        Note createdNote = noteService.createNote(userId, note);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdNote);
    }

    // READ
    @GetMapping("/{userId}")
    public ResponseEntity<List<Note>> getNotes(
            @PathVariable Long userId) {

        List<Note> notes = noteService.getNotesByUser(userId);

        return ResponseEntity.ok(notes);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody Note note) {

        Note updated = noteService.updateNote(id, note);

        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id) {

        noteService.deleteNote(id);

        return ResponseEntity.noContent().build();
    }
}