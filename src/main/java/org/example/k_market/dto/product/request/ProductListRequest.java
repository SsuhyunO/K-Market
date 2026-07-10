package org.example.k_market.dto.product.request;

import lombok.*;
import org.example.k_market.enums.ProductSortType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListRequest {
    private int page;
    private ProductSortType sortType;
    private int cateId;
}
