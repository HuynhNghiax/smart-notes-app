package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.dto.SendNotificationRequest;
import com.example.smartnotesbackend.service.FirebaseService;
import com.example.smartnotesbackend.repository.DeviceTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin("*")
public class NotificationController {

    private final FirebaseService firebaseService;
    private final DeviceTokenRepository deviceTokenRepository;

    public NotificationController(FirebaseService firebaseService,
                                  DeviceTokenRepository deviceTokenRepository) {
        this.firebaseService = firebaseService;
        this.deviceTokenRepository = deviceTokenRepository;
    }

    /**
     * Send notification to all user's devices
     * POST /api/notification/send-to-user
     * Body: {
     *   "userId": 1,
     *   "title": "Test Notification",
     *   "body": "This is a test message",
     *   "noteId": "123"
     * }
     */
    @PostMapping("/send-to-user")
    public ResponseEntity<?> sendToUser(@RequestBody SendNotificationRequest request) {
        try {
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
            }

            // Get all device tokens for user
            List<String> deviceTokens = deviceTokenRepository.findByUserId(request.getUserId())
                    .stream()
                    .map(dt -> dt.getToken())
                    .collect(Collectors.toList());

            if (deviceTokens.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No device tokens found for user: " + request.getUserId()));
            }

            // Send notification to each device individually
            String noteId = request.getNoteId() != null ? request.getNoteId() : "";
            int successCount = 0;
            for (String token : deviceTokens) {
                try {
                    firebaseService.sendNotification(
                            token,
                            request.getTitle(),
                            request.getBody(),
                            noteId
                    );
                    successCount++;
                } catch (Exception e) {
                    // Continue with next token if one fails
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Notifications sent successfully",
                    "totalDevices", deviceTokens.size(),
                    "successCount", successCount,
                    "userId", request.getUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Send notification to specific device token
     * POST /api/notification/send-to-device
     * Body: {
     *   "deviceToken": "cNxL9Rt3ZZE:APA91bHv8U...",
     *   "title": "Test Notification",
     *   "body": "This is a test message",
     *   "noteId": "123"
     * }
     */
    @PostMapping("/send-to-device")
    public ResponseEntity<?> sendToDevice(@RequestBody SendNotificationRequest request) {
        try {
            if (request.getDeviceToken() == null || request.getDeviceToken().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "deviceToken is required"));
            }

            String noteId = request.getNoteId() != null ? request.getNoteId() : "";
            firebaseService.sendNotification(
                    request.getDeviceToken(),
                    request.getTitle(),
                    request.getBody(),
                    noteId
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Notification sent successfully",
                    "deviceToken", request.getDeviceToken()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Simple health check
     * GET /api/notification/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "Firebase notification service is running"));
    }

    /**
     * Send test notification to user (simple endpoint)
     * GET /api/notification/test/{userId}
     * All params are hardcoded as "test"
     */
    @GetMapping("/test/{userId}")
    public ResponseEntity<?> sendTestNotification(@PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid userId"));
            }

            // Get all device tokens for user
            List<String> deviceTokens = deviceTokenRepository.findByUserId(userId)
                    .stream()
                    .map(dt -> dt.getToken())
                    .collect(Collectors.toList());

            if (deviceTokens.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No device tokens found for user: " + userId));
            }

            // Send test notification to each device individually
            int successCount = 0;
            for (String token : deviceTokens) {
                try {
                    firebaseService.sendNotification(
                            token,
                            "test",
                            "test",
                            "test"
                    );
                    successCount++;
                } catch (Exception e) {
                    // Continue with next token if one fails
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Test notifications sent successfully",
                    "userId", userId,
                    "totalDevices", deviceTokens.size(),
                    "successCount", successCount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
