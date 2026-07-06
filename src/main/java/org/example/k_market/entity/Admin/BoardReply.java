package org.example.k_market.entity.Admin;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "boardReply")

public class BoardReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int replyNo;
    private Integer boardNo;
    private String content;
    private LocalDateTime createdAt;

    public void updateReply(String content) {
        this.content = content;
    }

}
