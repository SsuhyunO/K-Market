package org.example.k_market.controller.admin.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ProductSearchCommand;
import org.example.k_market.dto.product.request.ProductSearchRequest;
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
    public ResponseEntity<PageResponse<ProductListResponse>> getProductPageInfo(@ModelAttribute ProductSearchRequest pageRequest, HttpSession session) {
        return ResponseEntity.ok(
            productService.getProductPageInfo(
                ProductSearchCommand
                    .builder()
                    .request(pageRequest)
                    .sellerUid((String)session.getAttribute("loginMember"))
                    .role((String)session.getAttribute("loginMemberType"))
                    .build()
                )
            );
    }

    @GetMapping("/{prodNo}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable int prodNo) {
        return ResponseEntity.ok(productService.getProductDetail(prodNo));
    }
}
