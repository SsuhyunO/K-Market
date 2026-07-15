package org.example.k_market.dto.order.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyOrderItemResponse {
    private int orderItemNo;
    private int orderNo;
    private String createdAt;
    private String sellerUid;
    private String sellerName;
    private int prodNo;
    private String productName;
    private String optionText;
    private Integer thumb1FileId;
    private int quantity;
    private int price;
    private int total;
    private String itemStatus;
    private Integer shipmentNo;
    private String courierName;
    private String trackingNo;
    private String shipmentStatus;
    private boolean reviewed;
}
