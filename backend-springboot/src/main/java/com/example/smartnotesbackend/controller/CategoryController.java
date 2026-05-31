package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.dto.CategoryDTO;
import com.example.smartnotesbackend.service.CategoryService;
import com.example.smartnotesbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;

    public CategoryController(CategoryService categoryService, AuthService authService) {
        this.categoryService = categoryService;
        this.authService = authService;
    }
// API: Tạo category mới cho user
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestHeader("Authorization") String token,
                                            @RequestBody Map<String, String> body) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            String name = body.get("name");
            String description = body.get("description");
            String colorCode = body.get("colorCode");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên danh mục không được để trống"));
            }

            CategoryDTO category = categoryService.createCategory(userId, name, description, colorCode);
            return ResponseEntity.ok(Map.of("message", "CREATE_SUCCESS", "category", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
//API: Lấy danh sách category của user đang đăng nhập
    @GetMapping("/list")
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization") String token) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            List<CategoryDTO> categories = categoryService.getCategoriesByUser(userId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
//API: Cập nhật thông tin category
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<?> updateCategory(@RequestHeader("Authorization") String token,
                                            @PathVariable Long categoryId,
                                            @RequestBody Map<String, String> body) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            String name = body.get("name");
            String description = body.get("description");
            String colorCode = body.get("colorCode");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên danh mục không được để trống"));
            }

            CategoryDTO category = categoryService.updateCategory(categoryId, userId, name, description, colorCode);
            return ResponseEntity.ok(Map.of("message", "UPDATE_SUCCESS", "category", category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
//API: Xóa category theo ID
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<?> deleteCategory(@RequestHeader("Authorization") String token,
                                            @PathVariable Long categoryId) {
        try {
            Long userId = authService.extractUserIdFromToken(token.replace("Bearer ", ""));
            String result = categoryService.deleteCategory(categoryId, userId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}