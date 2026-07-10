package org.example.k_market.dto.product.request;

import lombok.*;
import org.example.k_market.service.product.ProdSearchType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {
    private int page = 1;
    private ProdSearchType type;
    private String keyword;
}
