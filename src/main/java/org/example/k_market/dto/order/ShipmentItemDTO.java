package org.example.k_market.dto.order;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItemDTO {
    private int shipmentItemNo;
    private int shipmentNo;
    private int orderItemNo;
    private int quantity;
}
