package org.example.k_market.dto.cart;

import lombok.*;
import org.example.k_market.entity.cart.Cart;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private int cartNo;
    private String memberUid;
    private int prodNo;
    private int count;
    private int price;
    private int total;
    private String createdAt;

    public Cart toEntity() {
        return Cart.builder()
                .cartNo(cartNo)
                .memberUid(memberUid)
                .prodNo(prodNo)
                .count(count)
                .price(price)
                .total(total)
                .build();
    }
}
