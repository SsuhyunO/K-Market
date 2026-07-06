package org.example.k_market.dto.coupon;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {
    private int couponNo;
    private String couponType;
    private String sellerUid;
    private String benefit;
    private int issuedCnt;
    private int usedCnt;
    private String status;
    private String notice;
    private String name;
    private String expireDate;
    private String createdAt;
    private String updatedAt;
}
