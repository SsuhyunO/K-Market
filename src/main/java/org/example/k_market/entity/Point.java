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
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pointNo;

    private String memberUid;
    private int orderNo;
    private int point;
    private String content;
    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String expireDate;
}
