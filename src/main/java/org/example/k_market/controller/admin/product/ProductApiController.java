package org.example.k_market.controller.admin.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.request.PageRequest;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ProductListResponse>> getProductPageInfo(@ModelAttribute PageRequest pageRequest) {
        return ResponseEntity
            .ok(productService.getProductPageInfo(pageRequest));
    }

    @GetMapping("/{prodNo}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable int prodNo) {
        return ResponseEntity.ok(productService.getProductDetail(prodNo));
    }
}
