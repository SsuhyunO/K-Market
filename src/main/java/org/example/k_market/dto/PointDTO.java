package org.example.k_market.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointDTO {
    private Long pointNo;
    private String memberUid;
    private Integer orderNo;   // 사용/적립이 주문과 무관할 수도 있으니 nullable
    private int point;         // 적립 +, 사용 -
    private String content;    // "적립" / "사용"
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime expireDate;
    private String memberName;
    private int pointBalance;  // 잔여 포인트 (추가)
}
