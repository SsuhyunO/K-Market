package org.example.k_market.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequestDTO {
    private String receiver;
    private String phone;
    private String zipCode;
    private String addr1;
    private String addr2;
    private String orderNote;
    private String payMethod;
    private Long couponIssueId;
    private Integer targetVariantId; // PRODUCT 타입 쿠폰일 때 적용 대상 상품 (nullable)
    private Integer usedPoints;
    private List<OrderItemRequestDTO> items;
}
