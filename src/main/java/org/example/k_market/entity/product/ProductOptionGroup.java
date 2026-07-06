package org.example.k_market.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.product.ProductOptionGroupDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_option_group")
public class ProductOptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int prodNo;
    private String name;
    private boolean deleted;

    public ProductOptionGroupDTO toDTO() {
        return ProductOptionGroupDTO.builder()
                .id(id)
                .prodNo(prodNo)
                .name(name)
                .deleted(deleted)
                .build();
    }
}
