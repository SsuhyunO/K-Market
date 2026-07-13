package org.example.k_market.entity.product;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.k_market.dto.product.ProductNoticeValueDTO;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_notice_value")
public class ProductNoticeValue {
    @EmbeddedId
    private ProductNoticeValueId id;
    private String value;

    public void updateValue(String value) {
        this.value = value;
    }

    public ProductNoticeValueDTO toDTO() {
        return ProductNoticeValueDTO.builder()
            .noticeKey(id.getNoticeKey())
            .prodNo(id.getProdNo())
            .value(value)
            .build();
    }
}
