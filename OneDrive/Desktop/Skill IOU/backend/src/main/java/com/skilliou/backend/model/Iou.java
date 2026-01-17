package com.skilliou.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ious")
@Data
public class Iou {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The person GIVING help (Earns credits)
    @ManyToOne
    @JoinColumn(name = "giver_id", nullable = false)
    private User giver;

    // The person RECEIVING help (Owes credits)
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // What help was given?
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Double credits; // e.g., 2.0 credits

    // Status: PENDING, CONFIRMED, DECLINED
    @Column(nullable = false)
    private String status = "PENDING"; 

    @CreationTimestamp
    private LocalDateTime createdAt;
}