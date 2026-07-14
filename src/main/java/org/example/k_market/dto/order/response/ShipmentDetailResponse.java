package org.example.k_market.dto.order.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDetailResponse {
    private ShipmentListResponse shipment;
    private List<OrderLineResponse> items;
}
