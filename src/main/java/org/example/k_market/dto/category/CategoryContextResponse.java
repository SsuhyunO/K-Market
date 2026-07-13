package org.example.k_market.dto.category;

public record CategoryContextResponse(
    CategoryDTO parentCate,
    CategoryDTO subCate,
    Integer redirectCategoryId
) {
    public boolean hasRedirect() {
        return redirectCategoryId != null;
    }
}
