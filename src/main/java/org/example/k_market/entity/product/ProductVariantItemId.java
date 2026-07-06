package org.example.k_market.entity.product;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductVariantItemId {
    private int variantId;
    private int optionItemId;
}
