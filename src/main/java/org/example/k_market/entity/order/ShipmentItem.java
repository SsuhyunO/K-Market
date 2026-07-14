package org.example.k_market.entity.order;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipment_item")
public class ShipmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipmentItemNo;

    private int shipmentNo;
    private int orderItemNo;
    private int quantity;
}
