package org.example.k_market.dto.order;

import lombok.*;
import org.example.k_market.entity.order.OrderItem;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private int orderItemNo;
    private int orderNo;
    private int prodVariantId;
    private int count;
    private int price;
    private int total;
    private Integer prodNo;
    private String productName;
    private String optionText;
    private Integer originalPrice;
    private Integer discountRate;
    private int shippingFee;
    private String sellerUid;
    private String itemStatus;

    public OrderItem toEntity() {
        return OrderItem.builder()
                .orderItemNo(orderItemNo)
                .orderNo(orderNo)
                .prodVariantId(prodVariantId)
                .count(count)
                .price(price)
                .total(total)
                .prodNo(prodNo)
                .productName(productName)
                .optionText(optionText)
                .originalPrice(originalPrice)
                .discountRate(discountRate)
                .shippingFee(shippingFee)
                .sellerUid(sellerUid)
                .itemStatus(itemStatus)
                .build();
    }
}
