package org.example.k_market.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.product.ProductOptionItemDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_option_item")
public class ProductOptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int groupId;
    private String value;
    private boolean deleted;

    public ProductOptionItemDTO toDTO() {
        return ProductOptionItemDTO.builder()
                .id(id)
                .groupId(groupId)
                .value(value)
                .deleted(deleted)
                .build();
    }
}
