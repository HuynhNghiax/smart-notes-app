package com.example.smartnotesbackend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public void sendNotification(String deviceToken, String title, String body, String noteId) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("noteId", noteId)
                    .setToken(deviceToken)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent notification to device token. Message ID: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send notification to device token {}: {}", deviceToken, e.getMessage());
        }
    }

    public void sendNotificationMulticast(List<String> deviceTokens, String title, String body, String noteId) {
        if (deviceTokens.isEmpty()) {
            return;
        }

        try {
            com.google.firebase.messaging.MulticastMessage multicastMessage =
                    com.google.firebase.messaging.MulticastMessage.builder()
                            .addAllTokens(deviceTokens)
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .putData("noteId", noteId)
                            .build();

            com.google.firebase.messaging.BatchResponse response =
                    FirebaseMessaging.getInstance().sendMulticast(multicastMessage);

            logger.info("Sent notifications to {} devices. Success: {}, Failure: {}",
                    deviceTokens.size(), response.getSuccessCount(), response.getFailureCount());
        } catch (Exception e) {
            logger.error("Failed to send multicast notification: {}", e.getMessage());
        }
    }
}
