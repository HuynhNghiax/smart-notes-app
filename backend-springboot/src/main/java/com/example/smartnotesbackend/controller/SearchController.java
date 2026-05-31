package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.dto.NoteDTO;
import com.example.smartnotesbackend.service.SearchService;
import com.example.smartnotesbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final AuthService authService;

    public SearchController(SearchService searchService, AuthService authService) {
        this.searchService = searchService;
        this.authService = authService;
    }

    // API: Tìm kiếm ghi chú theo từ khóa
    @GetMapping("/notes")
    public ResponseEntity<?> searchNotes(@RequestHeader("Authorization") String token,
                                         @RequestParam String keyword) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            List<NoteDTO> notes = searchService.searchNotes(userId, keyword);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    // API: Lấy danh sách ghi chú đã ghim (pinned notes)
    @GetMapping("/pinned")
    public ResponseEntity<?> getPinnedNotes(@RequestHeader("Authorization") String token) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            List<NoteDTO> pinnedNotes = searchService.getPinnedNotes(userId);
            return ResponseEntity.ok(pinnedNotes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    // API: Lấy danh sách ghi chú theo category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getNotesByCategory(@RequestHeader("Authorization") String token,
                                                @PathVariable Long categoryId) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            List<NoteDTO> notes = searchService.getNotesByCategory(userId, categoryId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }
}