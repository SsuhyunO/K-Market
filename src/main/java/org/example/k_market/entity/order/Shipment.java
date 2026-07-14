package org.example.k_market.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipment")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipmentNo;

    private int orderNo;
    @Column(length = 20)
    private String sellerUid;
    @Column(length = 50, nullable = false)
    private String courierName;
    @Column(length = 100, nullable = false)
    private String trackingNo;
    @Column(length = 30, nullable = false)
    private String status;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
