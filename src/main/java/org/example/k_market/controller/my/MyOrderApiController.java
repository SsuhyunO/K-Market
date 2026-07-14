package org.example.k_market.controller.my;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.order.request.ClaimRequest;
import org.example.k_market.dto.order.request.MyOrderSearchRequest;
import org.example.k_market.dto.order.response.MyOrderItemResponse;
import org.example.k_market.dto.order.response.SimpleMessageResponse;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.service.order.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my/order/api")
@RequiredArgsConstructor
public class MyOrderApiController {

    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<MyOrderItemResponse>> getMyOrders(
        @ModelAttribute MyOrderSearchRequest request,
        HttpSession session
    ) {
        return ResponseEntity.ok(orderService.getMyOrderItems(getMemberUid(session), request));
    }

    @PostMapping("/{orderItemNo}/confirm")
    public ResponseEntity<SimpleMessageResponse> confirmPurchase(
        @PathVariable int orderItemNo,
        HttpSession session
    ) {
        orderService.confirmPurchase(getMemberUid(session), orderItemNo);
        return ResponseEntity.ok(new SimpleMessageResponse("구매확정이 완료되었습니다."));
    }

    @PostMapping("/{orderItemNo}/claim")
    public ResponseEntity<SimpleMessageResponse> requestClaim(
        @PathVariable int orderItemNo,
        @RequestBody ClaimRequest request,
        HttpSession session
    ) {
        orderService.requestClaim(getMemberUid(session), orderItemNo, request);
        return ResponseEntity.ok(new SimpleMessageResponse("요청이 접수되었습니다."));
    }

    private String getMemberUid(HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberUid;
    }
}
