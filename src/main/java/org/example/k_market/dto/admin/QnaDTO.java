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
     * boardType 예:
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
     * boardType 예:
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
     * HTML name="category1" 값 처리
     */
    public void setCategory1(String category1) {

        String category2 = getCategory2();

        if (category1 == null || category1.isBlank()) {
            this.boardType = null;
            return;
        }

        this.boardType = createBoardType(
                category1.trim(),
                category2
        );
    }

    /**
     * HTML name="category2" 값 처리
     */
    public void setCategory2(String category2) {

        String category1 = getCategory1();

        if (category2 == null || category2.isBlank()) {

            if (category1.isBlank()) {
                this.boardType = null;
            } else {
                this.boardType = "qna/" + category1;
            }

            return;
        }

        this.boardType = createBoardType(
                category1,
                category2.trim()
        );
    }

    private String createBoardType(
            String category1,
            String category2
    ) {
        if (category1 == null || category1.isBlank()) {
            return null;
        }

        if (category2 == null || category2.isBlank()) {
            return "qna/" + category1;
        }

        return "qna/" + category1 + "/" + category2;
    }

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

        return memberUid.substring(
                0,
                memberUid.length() - 2
        ) + "**";
    }

    public boolean isAnswered() {
        return answer != null && !answer.isBlank();
    }

    public String getStatus() {
        return isAnswered() ? "답변완료" : "검토중";
    }
}