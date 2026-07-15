package org.example.k_market.dto.order.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineResponse {
    private int orderItemNo;
    private int orderNo;
    private int prodVariantId;
    private Integer prodNo;
    private String prodName;
    private String optionText;
    private Integer thumb1FileId;
    private String sellerUid;
    private String sellerName;
    private int count;
    private int originalPrice;
    private int discountRate;
    private int discountAmount;
    private int price;
    private int total;
    private int shippingFee;
    private String itemStatus;
    private Integer shipmentNo;
    private String courierName;
    private String trackingNo;
    private String shipmentStatus;
    private String createdAt;
}
