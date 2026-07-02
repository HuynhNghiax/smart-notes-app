package com.example.smartnotesbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartNotesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartNotesBackendApplication.class, args);
    }

}