package com.example.smartnotesbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// BUG FIX #3: Tạo NoteDTO để tránh trả entity thô ra ngoài.
// Entity Note có @ManyToOne Category → Category có @OneToMany List<Tag>
// → serialize thẳng sẽ gây LazyInitializationException hoặc vòng lặp JSON vô hạn.
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
}