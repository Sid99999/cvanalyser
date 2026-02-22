package com.example.hello_spring.cv.dto;

import java.time.Instant;

public class CvResponse {

    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Instant uploadedAt;

    // Owner info (SAFE subset only)
    private Long userId;
    private String username;

    // =========================
    // Constructors
    // =========================

    public CvResponse() {}

    public CvResponse(Long id,
                      String title,
                      String fileName,
                      String fileType,
                      Instant uploadedAt,
                      Long userId,
                      String username) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedAt = uploadedAt;
        this.userId = userId;
        this.username = username;
    }

    // =========================
    // Getters & Setters
    // =========================

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
