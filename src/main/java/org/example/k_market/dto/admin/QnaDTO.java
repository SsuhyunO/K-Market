package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QnaDTO {

    private int boardNo;
    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private Integer fileId;
    private LocalDateTime createdAt;

    private String answer;
    private LocalDateTime answeredAt;

    /**
     * boardType 예시:
     * qna/회원/가입
     */
    public String getCategory1() {

        if (boardType == null || boardType.isBlank()) {
            return "";
        }

        String[] parts = boardType.split("/", 3);

        return parts.length >= 2 ? parts[1] : "";
    }

    /**
     * boardType 예시:
     * qna/회원/가입
     */
    public String getCategory2() {

        if (boardType == null || boardType.isBlank()) {
            return "";
        }

        String[] parts = boardType.split("/", 3);

        return parts.length >= 3 ? parts[2] : "";
    }

    /**
     * 관리자 화면에 작성자 UID 일부만 표시
     *
     * chhak -> chh**
     */
    public String getMaskedMemberUid() {

        if (memberUid == null || memberUid.isBlank()) {
            return "-";
        }

        if (memberUid.length() == 1) {
            return "*";
        }

        if (memberUid.length() == 2) {
            return memberUid.substring(0, 1) + "*";
        }

        return memberUid.substring(0, memberUid.length() - 2) + "**";
    }

    /**
     * 답변 여부
     */
    public boolean isAnswered() {
        return answer != null && !answer.isBlank();
    }

    /**
     * 화면 표시 상태
     */
    public String getStatus() {
        return isAnswered() ? "답변완료" : "검토중";
    }
}