package org.example.k_market.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointDTO {
    private int pointNo;
    private String memberUid;
    private int orderNo;
    private int point;
    private String content;
    private String note;
    private String createdAt;
    private String expireDate;
}
