package com.skilliou.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Maps to the 'users' table in MySQL
@Data // Lombok: Auto-generates Getters, Setters, toString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(name = "trust_score")
    private int trustScore = 100; // Default start score

    @CreationTimestamp // Automatically sets time when saved
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}