package org.example.k_market.dto.order.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentListResponse {
    private int shipmentNo;
    private Integer orderItemNo;
    private int orderNo;
    private String sellerUid;
    private String sellerName;
    private Integer prodNo;
    private String prodName;
    private Integer thumb1FileId;
    private int quantity;
    private int itemTotal;
    private int shippingFee;
    private String itemStatus;
    private String receiver;
    private String phone;
    private String address;
    private String courierName;
    private String trackingNo;
    private String status;
    private String shippedAt;
    private String createdAt;
    private int itemCount;
}
