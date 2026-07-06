package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.ProductVariant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private int id;
    private int prodNo;
    private int stock;
    private String status;

    public ProductVariant toEntity() {
        return ProductVariant.builder()
                .id(id)
                .prodNo(prodNo)
                .stock(stock)
                .status(status)
                .build();
    }
}
