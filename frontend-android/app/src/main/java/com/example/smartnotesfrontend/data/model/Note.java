package com.example.smartnotesfrontend.data.model;

public class Note {

    private Long id;
    private String title;
    private String content;
    private ScheduleMode scheduleMode;
    private String scheduleDate; // Using String for simplicity with Retrofit/Gson if no custom adapter
    private String notifyTime;
    private Long categoryId;
    private Category category;

    public Note() {
    }

    public Note(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
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

    public ScheduleMode getScheduleMode() {
        return scheduleMode;
    }

    public void setScheduleMode(ScheduleMode scheduleMode) {
        this.scheduleMode = scheduleMode;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}