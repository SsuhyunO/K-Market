package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {

    private int boardNo;
    private String memberUid;
    private String boardType;   // 예: "notice/고객서비스"
    private String title;
    private String content;
    private Integer fileId;
    private LocalDateTime createdAt;

    /**
     * boardType = "notice/고객서비스" 에서 "고객서비스"만 추출
     */
    public String getType() {
        if (boardType == null) return "";
        String[] parts = boardType.split("/");
        return parts.length >= 2 ? parts[1] : "";
    }
}
