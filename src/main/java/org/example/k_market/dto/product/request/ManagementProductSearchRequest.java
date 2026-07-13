package org.example.k_market.dto.product.request;

import lombok.*;
import org.example.k_market.enums.product.ProductSearchType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementProductSearchRequest {
    private int page = 1;
    private ProductSearchType type;
    private String keyword;
}
