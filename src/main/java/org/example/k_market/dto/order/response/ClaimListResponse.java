package org.example.k_market.dto.order.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimListResponse {
    private int claimNo;
    private int orderNo;
    private int orderItemNo;
    private String memberUid;
    private String memberName;
    private String productName;
    private String optionText;
    private int quantity;
    private String claimType;
    private String claimContent;
    private Integer fileId;
    private String claimStatus;
    private String itemStatus;
    private Integer shipmentNo;
    private String shipmentStatus;
    private String courierName;
    private String trackingNo;
    private String receiver;
    private String phone;
    private String zipCode;
    private String addr1;
    private String addr2;
    private String requestedAt;
    private String processedAt;
}
