package org.example.k_market.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPageRequest {
    private List<OrderItemRequest> items;   // 바로구매든 장바구니든 여기로 통일

    // 배송정보
    private String recvName;
    private String recvPhone;
    private String zipCode;
    private String addr1;
    private String addr2;
    private String orderNote;

    // 할인정보
    private Integer usedPoints;
    private Integer couponId;

    // 결제정보
    private String payMethod;
}