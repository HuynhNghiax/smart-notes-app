package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.Note;
import com.example.smartnotesbackend.repository.NoteRepository;
import com.example.smartnotesbackend.repository.DeviceTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(NoteSchedulerService.class);
    private final NoteRepository noteRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FirebaseService firebaseService;
    private final NoteService noteService;

    public NoteSchedulerService(NoteRepository noteRepository,
                                 DeviceTokenRepository deviceTokenRepository,
                                 FirebaseService firebaseService,
                                 NoteService noteService) {
        this.noteRepository = noteRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.firebaseService = firebaseService;
        this.noteService = noteService;
    }

    // Runs every minute to catch scheduled times precisely
    @Scheduled(cron = "0 * * * * ?")
    public void processScheduledNotes() {
        LocalDateTime now = LocalDateTime.now();
        List<Note> notes = noteRepository.findDueNotes(now);

        logger.info("Found {} due scheduled notes at {}", notes.size(), now);

        for (Note note : notes) {
            try {
                // Get tokens
                List<String> deviceTokens = deviceTokenRepository.findByUserId(note.getUserId())
                        .stream()
                        .map(dt -> dt.getToken())
                        .collect(Collectors.toList());

                if (!deviceTokens.isEmpty()) {
                    String title = note.getTitle() != null ? note.getTitle() : "Reminder";
                    String body = note.getContent() != null ? note.getContent() : "You have a scheduled note";

                    int success = 0;
                    for (String token : deviceTokens) {
                        try {
                            firebaseService.sendNotification(token, title, body, note.getId().toString());
                            success++;
                        } catch (Exception e) {
                            logger.warn("Failed to send to token {}: {}", token, e.getMessage());
                        }
                    }

                    logger.info("Sent notification for note id={} to {} devices (success={})",
                            note.getId(), deviceTokens.size(), success);
                } else {
                    logger.warn("No device tokens for user {} (note id={})", note.getUserId(), note.getId());
                }

                // Update nextScheduledAt
                note.setNextScheduledAt(noteService.computeNextScheduledAt(note));
                noteRepository.save(note);

            } catch (Exception e) {
                logger.error("Error processing scheduled note id={}: {}", note.getId(), e.getMessage());
            }
        }
    }
}