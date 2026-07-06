package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductNoticeValueId;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductNoticeValueDTO {
    private String noticeKey;
    private int prodNo;
    private String value;

    public ProductNoticeValue toEntity() {
        return ProductNoticeValue.builder()
                .id(new ProductNoticeValueId(noticeKey, prodNo))
                .value(value)
                .build();
    }
}
