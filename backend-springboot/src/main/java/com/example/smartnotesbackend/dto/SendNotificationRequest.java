package com.example.smartnotesbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    private Long userId;           // Send to all user's devices
    private String deviceToken;    // Or send to specific device
    private String title;          // Notification title
    private String body;           // Notification body
    private String noteId;         // Optional: link to note
}
