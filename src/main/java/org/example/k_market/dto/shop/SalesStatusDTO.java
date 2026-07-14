package org.example.k_market.dto.shop;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesStatusDTO {
    private int no;              // 화면 표시용 번호
    private String sellerUid;
    private String companyName;  // 상호명
    private String bizRegNo;     // 사업자등록번호

    private int orderCount;      // 주문건수 (판매자 상품이 포함된 distinct 주문 수)
    private int paidCount;       // 결제완료
    private int shippingCount;   // 배송중
    private int deliveredCount;  // 배송완료
    private int confirmedCount;  // 구매확정

    private long orderTotal;     // 주문합계 (해당 판매자 order_item.total 합)
    private long salesTotal;     // 매출합계 (구매확정(CONFIRMED) 건의 total 합)
}