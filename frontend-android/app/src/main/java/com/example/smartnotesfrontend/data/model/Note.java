package com.example.smartnotesfrontend.data.model;

public class Note {

    private Long id;

    private String title;

    private String content;

    private Long userId;

    private Category category;

    private String createdAt;

    private String updatedAt;

    private Boolean isPinned;

    public Note() {
    }

    public Note(Long id,
                String title,
                String content,
                Long userId,
                Category category,
                String createdAt,
                String updatedAt,
                Boolean isPinned) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPinned = isPinned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }
}