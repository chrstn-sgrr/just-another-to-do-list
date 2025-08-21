package com.example.product_api;

import java.time.LocalDateTime;

public class Todo {
    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime createdDate;
    private String priority; // HIGH, MEDIUM, LOW

    public Todo() {
        this.completed = false;
        this.createdDate = LocalDateTime.now();
        this.priority = "MEDIUM";
    }

    public Todo(String title) {
        this();
        this.title = title;
    }

    public Todo(String title, String priority) {
        this(title);
        this.priority = priority;
    }

    // Getters and Setters
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", createdDate=" + createdDate +
                ", priority='" + priority + '\'' +
                '}';
    }
}
