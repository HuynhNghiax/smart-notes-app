package com.example.smartnotesbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Column(name = "schedule_mode")
    @Enumerated(EnumType.STRING)
    private ScheduleMode scheduleMode;

    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    @Column(name = "notify_time")
    private LocalTime notifyTime;

    @Column(name = "next_scheduled_at")
    private LocalDateTime nextScheduledAt;

    public Note() {}
}