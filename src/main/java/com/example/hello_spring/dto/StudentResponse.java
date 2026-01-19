package com.example.hello_spring.dto;
import java.time.Instant;

public class StudentResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Instant createdAt;

    // Default constructor
    public StudentResponse() {}

    // Convenience constructor
    public StudentResponse(
            Long id,
            String name,
            String email,
            Integer age,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
