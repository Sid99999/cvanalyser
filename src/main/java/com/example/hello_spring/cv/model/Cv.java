package com.example.hello_spring.cv.model;

import com.example.hello_spring.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "cvs")
public class Cv {

    // =========================
    // PRIMARY KEY
    // =========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // CV METADATA
    // =========================
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    /**
     * Absolute or relative path where the file is stored.
     * AI and extraction logic NEVER read directly from DB blobs.
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * Extracted plain text from CV (PDF / DOCX).
     * - Large content
     * - Written once
     * - Read many times
     * - Nullable for legacy CVs
     */
    @Column(columnDefinition = "TEXT")
    private String extractedText;

    // =========================
    // AUDIT
    // =========================
    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    // =========================
    // OWNERSHIP
    // =========================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // =========================
    // CONSTRUCTOR (JPA ONLY)
    // =========================
    protected Cv() {
        // Required by JPA
    }

    // =========================
    // FACTORY METHOD
    // =========================
    public static Cv create(
            String title,
            String fileName,
            String fileType,
            Long fileSize,
            User owner
    ) {
        Cv cv = new Cv();
        cv.title = title;
        cv.fileName = fileName;
        cv.fileType = fileType;
        cv.fileSize = fileSize;
        cv.owner = owner;
        return cv;
    }

    // =========================
    // ENTITY LIFECYCLE
    // =========================
    @PrePersist
    protected void onUpload() {
        this.uploadedAt = Instant.now();
    }

    // =========================
    // DOMAIN METHODS
    // =========================

    /**
     * Set file storage location after physical save.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Persist extracted CV text.
     * Called exactly once after upload.
     */
    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    // =========================
    // GETTERS (NO SETTERS FOR CORE FIELDS)
    // =========================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public User getOwner() {
        return owner;
    }
}
