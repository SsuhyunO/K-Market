package org.example.k_market.dto.board;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private int boardNo;
    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private int fileId;
    private String createdAt;
}
