package org.example.k_market.dto.order;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemViewDTO {

    private Integer prodVariantId;   // 결제 시 다시 서버로 넘겨야 하니 hidden으로 필요
    private String thumbnailUrl;
    private String productName;
    private String optionText;       // "블랙 / L"
    private Integer quantity;
    private Integer price;           // 판매가 (개당)
    private Integer discountRate;    // 할인율(%)
    private Integer point;           // 적립 포인트
    private boolean freeShipping;
    private Integer shippingFee;
    private Integer lineTotal;       // 이 줄의 총액 (할인 적용된 가격 * 수량)
}