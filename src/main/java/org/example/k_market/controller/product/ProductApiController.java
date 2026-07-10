package org.example.k_market.controller.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.request.ProductListRequest;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/api")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ProductListResponse>> getProducts(@ModelAttribute ProductListRequest request) {
        return ResponseEntity.ok(productService.getProductPageInfo(request));
    }
}
