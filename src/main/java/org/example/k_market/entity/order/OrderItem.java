package org.example.k_market.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.order.OrderItemDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemNo;
    private int orderNo;
    private int prodVariantId;
    private int count;
    private int price;
    private int total;
    private int shippingFee;
    @Column(length = 20, nullable = false)
    private String sellerUid;
    @Column(length = 30, nullable = false)
    private String itemStatus;

    public OrderItemDTO toDTO() {
        return OrderItemDTO.builder()
                .orderItemNo(orderItemNo)
                .orderNo(orderNo)
                .prodVariantId(prodVariantId)
                .count(count)
                .price(price)
                .total(total)
                .shippingFee(shippingFee)
                .sellerUid(sellerUid)
                .itemStatus(itemStatus)
                .build();
    }
}
