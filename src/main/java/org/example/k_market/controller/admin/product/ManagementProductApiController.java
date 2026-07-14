package org.example.k_market.controller.admin.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ManagementProductSearchCommand;
import org.example.k_market.dto.product.request.ManagementProductSearchRequest;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ManagementProductListResponse;
import org.example.k_market.service.product.ProductService;
import org.example.k_market.service.product.ProductRemovalResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product/api")
@RequiredArgsConstructor
public class ManagementProductApiController {

    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ManagementProductListResponse>> getProductPageInfo(@ModelAttribute ManagementProductSearchRequest pageRequest, HttpSession session) {
        return ResponseEntity.ok(
            productService.getProductPageInfoForManagement(
                ManagementProductSearchCommand
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
        return ResponseEntity.ok(productService.getProductDetailForManagement(prodNo));
    }

    @DeleteMapping
    public ResponseEntity<ProductRemovalResult> removeProducts(
        @RequestParam("productNo") List<Integer> productNos
    ) {
        return ResponseEntity.ok(productService.remove(productNos));
    }
}
