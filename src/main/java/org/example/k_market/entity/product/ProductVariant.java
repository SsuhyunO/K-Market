package org.example.k_market.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.product.ProductVariantDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_variant")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int prodNo;
    private int stock;
    private String status;

    public ProductVariantDTO toDTO() {
        return ProductVariantDTO.builder()
                .id(id)
                .prodNo(prodNo)
                .stock(stock)
                .status(status)
                .build();
    }
}
