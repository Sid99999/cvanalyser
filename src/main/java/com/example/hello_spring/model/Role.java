package com.example.hello_spring.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // ROLE_USER, ROLE_ADMIN

    // ===== Constructors =====
    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Always store roles with ROLE_ prefix
    public void setName(String name) {
        this.name = name;
    }
}
