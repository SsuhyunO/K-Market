package org.example.k_market.entity.product;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.k_market.dto.product.ProductVariantItemDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_variant_item")
public class ProductVariantItem {
    @EmbeddedId
    private ProductVariantItemId id;

    public ProductVariantItemDTO toDTO() {
        return ProductVariantItemDTO.builder()
                .variantId(id.getVariantId())
                .optionItemId(id.getOptionItemId())
                .build();
    }
}
