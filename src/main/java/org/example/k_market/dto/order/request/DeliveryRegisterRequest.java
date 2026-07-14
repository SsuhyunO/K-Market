package org.example.k_market.dto.order.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRegisterRequest {
    private int orderNo;
    private String courierName;
    private String trackingNo;
    private List<Integer> orderItemNos;
}
