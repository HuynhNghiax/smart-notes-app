package com.example.smartnotesbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String testGemini(String promptText) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textObj = new HashMap<>();
            textObj.put("text", promptText);
            Map<String, Object> partsObj = new HashMap<>();
            partsObj.put("parts", List.of(textObj));
            Map<String, Object> contentsObj = new HashMap<>();
            contentsObj.put("contents", List.of(partsObj));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(contentsObj, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ĐÃ SỬA: Trả về List<String> thay vì String
    public List<String> getSummary(String noteContent) {
        try {
            String customPrompt = "Hãy tách nội dung sau thành các việc cần làm. " +
                    "Mỗi việc bắt đầu bằng dấu gạch ngang (-). Không giải thích gì thêm.\n" +
                    "Nội dung: " + noteContent;

            String rawJson = testGemini(customPrompt);

            // --- LOG RA TERMINAL ĐỂ XEM AI ĐANG GỬI GÌ ---
            System.out.println("DEBUG - Phản hồi thô từ Google: " + rawJson);

            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(rawJson);

            // Kiểm tra lỗi 429 hoặc lỗi cấu trúc
            if (root.has("error")) {
                return List.of("Lỗi Google (429/Quota): " + root.path("error").path("message").asText());
            }

            // Lấy text từ AI
            // Lấy text từ AI
            String resultText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            System.out.println("DEBUG - Nội dung AI trả về: " + resultText);

            List<String> list = new ArrayList<>();
            String[] lines = resultText.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("-")) {
                    list.add(trimmed.replaceFirst("-", "").trim());
                }
            }

            // --- CẦU NỐI DEBUG ---
            // Nếu danh sách rỗng, ta trả về chính nội dung AI đã gửi để kiểm tra
            if (list.isEmpty()) {
                return List.of("AI trả về: " + resultText);
            }

            return list;

        } catch (Exception e) {
            return List.of("Lỗi hệ thống: " + e.getMessage());
        }
    }
    }