package org.example.k_market.dto.product.request;

import lombok.*;
import org.example.k_market.enums.product.ProductSortType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {
    private int page;
    private ProductSortType sortType;
    private String keyword;
    private boolean name;
    private boolean description;
    private boolean price;
    private Integer minPrice;
    private Integer maxPrice;
}
