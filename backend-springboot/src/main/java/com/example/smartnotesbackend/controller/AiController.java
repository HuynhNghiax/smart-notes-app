package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.service.GeminiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;

    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<List<String>> summarizeNote(
            @RequestBody Map<String, String> request) {

        String content = request.get("content");
        List<String> summary = geminiService.getSummary(content);

        return ResponseEntity.ok(summary);
    }
}