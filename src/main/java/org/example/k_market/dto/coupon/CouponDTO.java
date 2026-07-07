package org.example.k_market.dto.coupon;

import lombok.*;
import org.example.k_market.entity.coupon.Coupon;

import java.time.LocalDate;

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
    private String startDate;

    // 추가 필드
    private int validDays;
    private String companyName;

    public Coupon toEntity(){
        return Coupon.builder()
                .couponNo(couponNo)
                .couponType(couponType)
                .sellerUid(sellerUid)
                .benefit(benefit)
                .issuedCnt(issuedCnt)
                .usedCnt(usedCnt)
                .status(status)
                .notice(notice)
                .name(name)
                .expireDate(expireDate)
                .startDate(startDate)
                .build();
    }
}
