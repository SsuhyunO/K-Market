package org.example.k_market.dto.board;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board_replyDTO {
    private int replyNo;
    private int boardNo;
    private String content;
    private String createdAt;
}
