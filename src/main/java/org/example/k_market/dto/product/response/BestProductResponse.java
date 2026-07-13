package org.example.k_market.dto.product.response;

import org.example.k_market.entity.product.Product;

public record BestProductResponse(
    int prodNo,
    String name,
    int originalPrice,
    int discountRate,
    Integer thumbnailFileId
) {
    public static BestProductResponse from(Product product) {
        return new BestProductResponse(
            product.getProdNo(),
            product.getProdName(),
            product.getPrice(),
            product.getDiscount(),
            product.getThumb1FileId()
        );
    }

    public int salePrice() {
        return (int)(originalPrice - ((double)originalPrice * discountRate / 100));
    }
}
