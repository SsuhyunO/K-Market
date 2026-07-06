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
@Table(name = "board_reply")
public class BoardReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyNo;

    private Integer boardNo;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime rdate;

    public void updateReply(String content) {
        this.content = content;
    }
}