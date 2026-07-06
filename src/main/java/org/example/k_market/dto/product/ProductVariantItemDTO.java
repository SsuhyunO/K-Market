package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.entity.product.ProductVariantItemId;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantItemDTO {
    private int variantId;
    private int optionItemId;

    public ProductVariantItem toEntity() {
        return ProductVariantItem.builder()
                .id(new ProductVariantItemId(variantId, optionItemId))
                .build();
    }
}
