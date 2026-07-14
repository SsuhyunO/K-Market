package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryContextResponse;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ProductViewPageResponse;
import org.example.k_market.service.CategoryService;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ProductViewPageReader {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.KOREAN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);

    private final ProductService productService;
    private final CategoryService categoryService;
    private final Clock clock;

    public ProductViewPageResponse getViewPage(int prodNo) {
        ProductDetailResponse product = productService.getProductDetail(prodNo);
        CategoryContextResponse categoryContext = categoryService.getContext(product.getCateId());
        LocalDate today = LocalDate.now(clock);
        LocalDate expectedDeliveryDate = today.plusDays(3);

        return new ProductViewPageResponse(
            product,
            categoryContext.parentCate(),
            categoryContext.subCate(),
            today.format(DATE_FORMATTER),
            today.format(DAY_FORMATTER),
            expectedDeliveryDate.format(DATE_FORMATTER),
            expectedDeliveryDate.format(DAY_FORMATTER)
        );
    }
}
