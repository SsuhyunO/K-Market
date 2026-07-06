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
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardNo;

    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private int fileId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
