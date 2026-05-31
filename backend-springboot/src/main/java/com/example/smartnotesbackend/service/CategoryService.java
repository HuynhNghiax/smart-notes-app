package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.dto.CategoryDTO;
import com.example.smartnotesbackend.entity.Category;
import com.example.smartnotesbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Tạo danh mục mới
    public CategoryDTO createCategory(Long userId, String name, String description, String colorCode) {
        if (categoryRepository.findByNameAndUserId(name, userId).isPresent()) {
            throw new RuntimeException("CATEGORY_ALREADY_EXISTS");
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setDescription(description);
        category.setColorCode(colorCode);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    // Lấy tất cả danh mục của người dùng
    public List<CategoryDTO> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Cập nhật danh mục
    public CategoryDTO updateCategory(Long categoryId, Long userId, String name, String description, String colorCode) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        Optional<Category> existingWithSameName = categoryRepository.findByNameAndUserId(name, userId);
        if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(categoryId)) {
            throw new RuntimeException("CATEGORY_NAME_ALREADY_EXISTS");
        }

        category.setName(name);
        category.setDescription(description);
        category.setColorCode(colorCode);
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    // Xóa danh mục
    public String deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        categoryRepository.delete(category);
        return "DELETE_SUCCESS";
    }

    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColorCode(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}