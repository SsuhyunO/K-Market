package org.example.k_market.dto.product;

import java.util.List;

public record ProductInfoNoticeTemplate(
        String code,
        String name,
        List<ProductInfoNoticeField> fields
) {
}
