package org.example.k_market.dto.product.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequest {
    @Builder.Default
    private List<ProductVariantItemRequest> items = new java.util.ArrayList<>();
    private int stock;
    private String status;
}
