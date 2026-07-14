package org.example.k_market.dto.point.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointListResponse {
    private int pointNo;
    private String createdAt;
    private String content;
    private int orderNo;
    private int point;
    private String note;
    private String expireDate;
}
