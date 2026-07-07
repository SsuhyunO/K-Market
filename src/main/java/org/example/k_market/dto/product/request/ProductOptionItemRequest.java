package org.example.k_market.dto.product.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionItemRequest {
    private String clientKey;
    private String value;
}
