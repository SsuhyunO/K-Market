package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FaqDTO {

    private int boardNo;
    private String memberUid;
    private String boardType;
    private String title;
    private String content;
    private Integer fileId;
    private LocalDateTime createdAt;

    /**
     * boardType 예:
     * faq/회원/회원가입
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
     * faq/회원/회원가입
     */
    public String getCategory2() {

        if (boardType == null || boardType.isBlank()) {
            return "";
        }

        String[] parts = boardType.split("/", 3);

        return parts.length >= 3 ? parts[2] : "";
    }

    /**
     * 목록 화면 호환용
     *
     * ${faq.type} 사용 시 1차 유형 반환
     */
    public String getType() {
        return getCategory1();
    }

    /**
     * HTML의 name="category1" 값 처리
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
     * HTML의 name="category2" 값 처리
     */
    public void setCategory2(String category2) {

        String category1 = getCategory1();

        if (category2 == null || category2.isBlank()) {

            if (category1.isBlank()) {
                this.boardType = null;
            } else {
                this.boardType = "faq/" + category1;
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
            return "faq/" + category1;
        }

        return "faq/" + category1 + "/" + category2;
    }
}