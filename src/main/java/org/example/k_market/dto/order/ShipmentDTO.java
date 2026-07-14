package org.example.k_market.dto.order;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private int shipmentNo;
    private int orderNo;
    private String sellerUid;
    private String courierName;
    private String trackingNo;
    private String status;
    private String shippedAt;
    private String deliveredAt;
    private String createdAt;
}
