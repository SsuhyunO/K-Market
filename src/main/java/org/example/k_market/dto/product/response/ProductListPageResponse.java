package org.example.k_market.dto.product.response;

import org.example.k_market.dto.category.CategoryDTO;

public record ProductListPageResponse(
    CategoryDTO parentCate,
    CategoryDTO subCate
) {
    public String categoryName() {
        return subCate != null ? subCate.getName() : "상품목록";
    }
}
