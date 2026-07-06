package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.ProductOptionGroup;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionGroupDTO {
    private int id;
    private int prodNo;
    private String name;
    private boolean deleted;

    public ProductOptionGroup toEntity() {
        return ProductOptionGroup.builder()
                .id(id)
                .prodNo(prodNo)
                .name(name)
                .deleted(deleted)
                .build();
    }
}
