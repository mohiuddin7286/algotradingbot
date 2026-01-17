package com.skilliou.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "skills")
@Data
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Python Tutoring"

    @Column(nullable = false)
    private String category; // e.g., "Education", "Tech", "Labor"
    
    // We don't need detailed timestamps for skills, keep it simple.
}