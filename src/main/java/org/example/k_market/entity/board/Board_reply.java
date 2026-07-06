package org.example.k_market.entity.board;

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
@Table(name = "board")
public class Board_reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int replyNo;

    private int boardNo;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
