package org.example.k_market.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "claim")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int claimNo;

    private int orderNo;
    private int orderItemNo;
    @Column(length = 30)
    private String claimType;
    private String claimContent;
    private Integer fileId;
    private int quantity;
    @Column(length = 30, nullable = false)
    private String claimStatus;
    @CreationTimestamp
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
