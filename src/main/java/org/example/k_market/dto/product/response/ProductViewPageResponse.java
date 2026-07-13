package org.example.k_market.dto.product.response;

import org.example.k_market.dto.category.CategoryDTO;

public record ProductViewPageResponse(
    ProductDetailResponse product,
    CategoryDTO parentCate,
    CategoryDTO subCate,
    String todayDate,
    String todayDayOfWeek,
    String expectedDeliveryDate,
    String expectedDeliveryDayOfWeek
) {
}
