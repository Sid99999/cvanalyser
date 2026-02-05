package com.example.hello_spring.cv.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public class CvResponse {

    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private String filePath;
    private Instant uploadedAt;

    // owner info (SAFE subset)
    private Long userId;
    private String username;

    // --- constructors ---
    public CvResponse() {}

    public CvResponse(Long id, String title, String fileName, String fileType,
                      String filePath, Instant uploadedAt,
                      Long userId, String username) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.userId = userId;
        this.username = username;
    }

    // --- getters & setters ---
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
