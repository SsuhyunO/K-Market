package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.order.OrderCreateRequestDTO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.request.ProductListRequest;
import org.example.k_market.dto.product.request.ProductSearchRequest;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.dto.product.response.ProductSearchResponse;
import org.example.k_market.service.product.ProductService;
import org.example.k_market.service.order.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product/api")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ProductListResponse>> getProducts(@ModelAttribute ProductListRequest request) {
        return ResponseEntity.ok(productService.getProductPageInfo(request));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSearchResponse>> searchProducts(@ModelAttribute ProductSearchRequest request) {
        return ResponseEntity.ok(productService.getProductSearchPageInfo(request));
    }

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderCreateRequestDTO req,
            HttpSession session) {
        try {
            String memberUid = (String) session.getAttribute("loginMember");

            int orderNo = orderService.createOrder(memberUid, req);
            return ResponseEntity.ok(Map.of("orderNo", orderNo));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("주문 생성 중 예외 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "주문 처리 중 오류가 발생했습니다."));
        }
    }
}
