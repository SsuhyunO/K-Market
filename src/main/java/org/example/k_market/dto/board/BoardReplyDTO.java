package org.example.k_market.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardReplyDTO {
    private int replyNo;
    private int boardNo;
    private String content;
    private LocalDateTime rdate;
}
