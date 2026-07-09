package org.example.k_market.dto.product.command;

import lombok.*;
import org.example.k_market.dto.product.request.ProductSearchRequest;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchCommand {
    private ProductSearchRequest request;
    private String sellerUid;
    private String role;
}
