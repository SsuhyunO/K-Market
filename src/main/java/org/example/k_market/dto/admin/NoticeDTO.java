package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {

    private int boardNo;
    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private Integer fileId;
    private LocalDateTime createdAt;

    /**
     * boardType = "notice/고객서비스"에서
     * "고객서비스"만 반환
     */
    public String getType() {

        if (boardType == null || boardType.isBlank()) {
            return "";
        }

        String prefix = "notice/";

        if (boardType.startsWith(prefix)) {
            return boardType.substring(prefix.length());
        }

        return boardType;
    }

    /**
     * HTML의 name="type" 값을 받아서
     * DB 저장 형식인 notice/{유형}으로 변환
     */
    public void setType(String type) {

        if (type == null || type.isBlank()) {
            this.boardType = null;
            return;
        }

        String trimmedType = type.trim();

        if (trimmedType.startsWith("notice/")) {
            this.boardType = trimmedType;
        } else {
            this.boardType = "notice/" + trimmedType;
        }
    }
}