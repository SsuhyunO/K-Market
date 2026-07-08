package org.example.k_market.dto.coupon;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponIssueDTO {
    private int issueNo;
    private int couponNo;
    private String memberUid;
    private int status;
    private String createdAt;
    private String updatedAt;

    // coupon join 컬럼
    private String couponType;
    private String sellerUid;
    private String benefit;
    private String couponName;
    private String startDate;
    private String expireDate;
    private String notice;
}
