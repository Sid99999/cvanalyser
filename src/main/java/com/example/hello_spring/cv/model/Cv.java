package com.example.hello_spring.cv.model;

import com.example.hello_spring.model.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cvs")
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Constructors
    public Cv() {}

    public Cv(String title, String fileName, String fileType, String filePath, User owner) {
        this.title = title;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.owner = owner;
    }

    // Getters & Setters (standard)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
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



    public User getOwner() {
        return owner;
    }



    public Instant getUploadedAt() {
        return uploadedAt;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void assignOwner(User user) {
        this.owner = user;
    }


}
