package org.example.k_market.dto.coupon;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon_issueDTO {
    private int issueNo;
    private int couponNo;
    private String memberUid;
    private int status;
    private String createdAt;
    private String updatedAt;
}
