package com.example.smartnotesbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.smartnotesbackend.entity.ScheduleMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String categoryColorCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isPinned;
    private ScheduleMode scheduleMode;
    private LocalDate scheduleDate;
    private LocalTime notifyTime;
    private LocalDateTime nextScheduledAt;
}
