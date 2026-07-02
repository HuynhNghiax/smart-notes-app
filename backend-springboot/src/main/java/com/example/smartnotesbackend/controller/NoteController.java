package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.dto.NoteDTO;
import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.service.AuthService;
import com.example.smartnotesbackend.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin("*")
public class NoteController {

    private final NoteService noteService;
    private final AuthService authService;

    public NoteController(NoteService noteService, AuthService authService) {
        this.noteService = noteService;
        this.authService = authService;
    }

    // CREATE
    @PostMapping
    public Note createNote(@RequestHeader("Authorization") String token,
                           @RequestBody Note note) {
        Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
        return noteService.createNote(userId, note);
    }

    // Create with DTO
    @PostMapping("/dto")
    public Note createNoteWithDto(@RequestHeader("Authorization") String token,
                                  @RequestBody NoteDTO noteDTO) {
        Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
        Note note = new Note();
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setScheduleMode(noteDTO.getScheduleMode());
        note.setScheduleDate(noteDTO.getScheduleDate());
        return noteService.createNote(userId, note);
    }

    // READ
    @GetMapping
    public List<Note> getNotes(@RequestHeader("Authorization") String token) {
        Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
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