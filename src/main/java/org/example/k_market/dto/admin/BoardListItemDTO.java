package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardListItemDTO {
    private long boardNo;
    private String category;      // 1차 카테고리 (고객서비스, 회원 등)
    private String title;
    private String memberUid;
    private LocalDateTime createdAt;
}
