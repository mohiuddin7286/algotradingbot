package com.skilliou.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger")
@Data
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Whose balance is changing?
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // How much? (+2.5 or -2.5)
    @Column(nullable = false)
    private Double creditChange;

    // Why? (e.g., "IOU #1 Confirmed")
    @Column(nullable = false)
    private String reason;

    @CreationTimestamp
    private LocalDateTime timestamp;
}