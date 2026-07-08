package org.example.k_market.controller.admin.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.response.PageResponse;
import org.example.k_market.dto.product.ProductDTO;
import org.example.k_market.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ProductDTO>> getProducts() {

        return ResponseEntity
            .ok(PageResponse
                .<ProductDTO>builder()
                .list(productService.getProductList()).build());
    }
}
