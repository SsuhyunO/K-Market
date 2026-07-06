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
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int boardNo;
    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private int filed;
    private LocalDateTime createdAt;

    public void updateBoard(String boardType, String title, String content, Integer fileId) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.fileId = fileId;
    }

}
