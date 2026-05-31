package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.service.NoteService;
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
    public Note createNote(@PathVariable Long userId,
                           @RequestBody Note note) {

        return noteService.createNote(userId, note);
    }

    // READ
    @GetMapping("/{userId}")
    public List<Note> getNotes(@PathVariable Long userId) {

        return noteService.getNotesByUser(userId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id,
                           @RequestBody Note note) {

        return noteService.updateNote(id, note);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id) {

        noteService.deleteNote(id);

        return "Deleted successfully";
    }
}