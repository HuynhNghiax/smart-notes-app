package com.example.smartnotesbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:firebase-adminsdk.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Use ClassPathResource to load from src/main/resources
                ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
                InputStream serviceAccount = resource.getInputStream();
                
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println(">>> [FIREBASE] Firebase Admin SDK initialized successfully");
            }
        } catch (Exception e) {
            System.out.println(">>> [FIREBASE ERROR] Failed to initialize Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
