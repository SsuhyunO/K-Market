package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.cart.CartAddRequest;
import org.example.k_market.dto.order.OrderCreateRequestDTO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.request.ProductListRequest;
import org.example.k_market.dto.product.request.ProductSearchRequest;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.dto.product.response.ProductSearchResponse;
import org.example.k_market.service.CartService;
import org.example.k_market.service.ProductService;
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
    private final CartService cartService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ProductListResponse>> getProducts(@ModelAttribute ProductListRequest request) {
        return ResponseEntity.ok(productService.getProductPageInfo(request));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSearchResponse>> searchProducts(@ModelAttribute ProductSearchRequest request) {
        return ResponseEntity.ok(productService.getProductSearchPageInfo(request));
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addCart(@RequestBody CartAddRequest request, HttpSession session) {
        try {
            String memberUid = (String) session.getAttribute("loginMember");
            if (memberUid == null || memberUid.isBlank()) {
                return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
            }

            cartService.add(memberUid, request);
            return ResponseEntity.ok(Map.of("message", "장바구니에 담았습니다."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("장바구니 담기 중 예외 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "장바구니 처리 중 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/cart")
    public ResponseEntity<?> removeCart(@RequestParam("cartNo") java.util.List<Integer> cartNos, HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }

        int removedCount = cartService.remove(memberUid, cartNos);
        return ResponseEntity.ok(Map.of("removedCount", removedCount));
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
