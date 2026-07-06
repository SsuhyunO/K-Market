package org.example.k_market.common.product;

import java.util.List;

public record ProductInfoNoticeTemplate(
        String code,
        String name,
        List<ProductInfoNoticeField> fields
) {
}
