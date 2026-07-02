package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.entity.User;
import com.example.smartnotesbackend.entity.ScheduleMode;
import com.example.smartnotesbackend.repository.NoteRepository;
import com.example.smartnotesbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        // Compute next scheduled time based on provided fields
        note.setNextScheduledAt(computeNextScheduledAt(note));

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
        
        // Update schedule fields if provided
        note.setScheduleMode(updatedNote.getScheduleMode());
        note.setScheduleDate(updatedNote.getScheduleDate());
        note.setNotifyTime(updatedNote.getNotifyTime());

        // Recompute next scheduled time
        note.setNextScheduledAt(computeNextScheduledAt(note));

        return noteRepository.save(note);
    }

    // DELETE
    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    // Compute next scheduled LocalDateTime based on scheduleMode, scheduleDate and notifyTime
    public LocalDateTime computeNextScheduledAt(Note note) {
        if (note.getScheduleMode() == null) return null;

        LocalDateTime now = LocalDateTime.now();
        LocalTime notifyTime = note.getNotifyTime() != null ? note.getNotifyTime() : LocalTime.of(9, 0);

        if (note.getScheduleMode() == ScheduleMode.EVERYDAY) {
            LocalDate today = LocalDate.now();
            LocalDateTime candidate = LocalDateTime.of(today, notifyTime);
            if (candidate.isAfter(now)) {
                return candidate;
            } else {
                return candidate.plusDays(1);
            }
        } else if (note.getScheduleMode() == ScheduleMode.SPECIFIC_DAY) {
            if (note.getScheduleDate() == null) return null;
            LocalDateTime candidate = LocalDateTime.of(note.getScheduleDate(), notifyTime);
            if (candidate.isAfter(now)) {
                return candidate;
            } else {
                // Specific day already passed
                return null;
            }
        }

        return null;
    }
}