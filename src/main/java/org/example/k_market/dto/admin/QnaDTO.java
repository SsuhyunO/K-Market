package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QnaDTO {

    /* 문의글 번호 */
    private Integer boardNo;

    /* 작성자 아이디 */
    private String memberUid;

    /* 문의 1차 유형 */
    private String category1;

    /* 문의 2차 유형 */
    private String category2;

    /* 문의 제목 */
    private String title;

    /* 문의 내용 */
    private String content;

    /* 관리자 답변 */
    private String answer;

    /* 문의 등록일 */
    private LocalDateTime createdAt;

    /* 답변 등록일 */
    private LocalDateTime answeredAt;

    /* 답변 여부 */
    public boolean isAnswered() {
        return answer != null
                && !answer.isBlank();
    }

    /* 문의 처리 상태 */
    public String getStatus() {
        return isAnswered()
                ? "답변완료"
                : "검토중";
    }

    /* 작성자 아이디 마스킹 */
    public String getMaskedMemberUid() {
        if (memberUid == null || memberUid.isBlank()) {
            return "-";
        }

        String normalizedMemberUid =
                memberUid.trim();

        if (normalizedMemberUid.length() == 1) {
            return "*";
        }

        if (normalizedMemberUid.length() == 2) {
            return normalizedMemberUid.substring(0, 1)
                    + "*";
        }

        return normalizedMemberUid.substring(
                0,
                normalizedMemberUid.length() - 2
        ) + "**";
    }
}