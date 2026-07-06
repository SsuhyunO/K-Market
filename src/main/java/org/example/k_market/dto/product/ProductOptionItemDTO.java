package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.ProductOptionItem;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionItemDTO {
    private int id;
    private int groupId;
    private String value;
    private boolean deleted;

    public ProductOptionItem toEntity() {
        return ProductOptionItem.builder()
                .id(id)
                .groupId(groupId)
                .value(value)
                .deleted(deleted)
                .build();
    }
}
