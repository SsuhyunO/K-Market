package org.example.k_market.dto.product.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionGroupRequest {
    private String clientKey;
    private String name;

    @Builder.Default
    private List<ProductOptionItemRequest> items = new java.util.ArrayList<>();
}
