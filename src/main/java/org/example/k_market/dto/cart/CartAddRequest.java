package org.example.k_market.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartAddRequest {
    private Integer prodVariantId;
    private Integer count;
}
