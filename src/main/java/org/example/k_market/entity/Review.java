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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewNo;

    private int orderItemNo;
    private String memberUid;
    private int prodNo;
    private int rating;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
