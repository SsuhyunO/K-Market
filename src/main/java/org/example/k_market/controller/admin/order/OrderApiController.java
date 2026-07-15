package org.example.k_market.controller.admin.order;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.order.request.ClaimActionRequest;
import org.example.k_market.dto.order.request.DeliveryRegisterRequest;
import org.example.k_market.dto.order.request.ManagementOrderSearchRequest;
import org.example.k_market.dto.order.response.ClaimListResponse;
import org.example.k_market.dto.order.response.ManagementOrderListResponse;
import org.example.k_market.dto.order.response.OrderDetailResponse;
import org.example.k_market.dto.order.response.OrderLineResponse;
import org.example.k_market.dto.order.response.ShipmentDetailResponse;
import org.example.k_market.dto.order.response.ShipmentListResponse;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.service.order.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/order/api")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ManagementOrderListResponse>> getOrderPageInfo(
        @ModelAttribute ManagementOrderSearchRequest pageRequest,
        HttpSession session
    ) {
        return ResponseEntity.ok(orderService.getOrderPageInfoForManagement(pageRequest, getSellerScopeUid(session)));
    }

    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable int orderNo, HttpSession session) {
        return ResponseEntity.ok(orderService.getOrderDetailForManagement(orderNo, getSellerScopeUid(session)));
    }

    @GetMapping("/{orderNo}/shippable-items")
    public ResponseEntity<List<OrderLineResponse>> getShippableItems(@PathVariable int orderNo, HttpSession session) {
        return ResponseEntity.ok(orderService.getShippableOrderLines(orderNo, getSellerScopeUid(session)));
    }

    @PostMapping("/shipments")
    public ResponseEntity<ShipmentDetailResponse> registerDelivery(@RequestBody DeliveryRegisterRequest request, HttpSession session) {
        return ResponseEntity.ok(orderService.registerDelivery(request, requireSellerUid(session)));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<PageResponse<ShipmentListResponse>> getDeliveryPageInfo(
        @ModelAttribute ManagementOrderSearchRequest pageRequest,
        HttpSession session
    ) {
        return ResponseEntity.ok(orderService.getShipmentPageInfo(pageRequest, getSellerScopeUid(session)));
    }

    @GetMapping("/deliveries/{shipmentNo}")
    public ResponseEntity<ShipmentDetailResponse> getDeliveryDetail(@PathVariable int shipmentNo, HttpSession session) {
        return ResponseEntity.ok(orderService.getShipmentDetail(shipmentNo, getSellerScopeUid(session)));
    }

    @GetMapping("/claims")
    public ResponseEntity<PageResponse<ClaimListResponse>> getClaimPageInfo(
        @ModelAttribute ManagementOrderSearchRequest pageRequest,
        HttpSession session
    ) {
        return ResponseEntity.ok(orderService.getClaimPageInfo(pageRequest, getSellerScopeUid(session)));
    }

    @GetMapping("/claims/{claimNo}")
    public ResponseEntity<ClaimListResponse> getClaimDetail(@PathVariable int claimNo, HttpSession session) {
        return ResponseEntity.ok(orderService.getClaimDetail(claimNo, getSellerScopeUid(session)));
    }

    @PostMapping("/claims/{claimNo}/approve")
    public ResponseEntity<ClaimListResponse> approveClaim(@PathVariable int claimNo, HttpSession session) {
        return ResponseEntity.ok(orderService.approveClaim(claimNo, requireSellerUid(session)));
    }

    @PostMapping("/claims/{claimNo}/reject")
    public ResponseEntity<ClaimListResponse> rejectClaim(@PathVariable int claimNo, HttpSession session) {
        return ResponseEntity.ok(orderService.rejectClaim(claimNo, requireSellerUid(session)));
    }

    @PostMapping("/claims/{claimNo}/reship")
    public ResponseEntity<ClaimListResponse> reshipExchangeClaim(
        @PathVariable int claimNo,
        @RequestBody ClaimActionRequest request,
        HttpSession session
    ) {
        return ResponseEntity.ok(orderService.reshipExchangeClaim(claimNo, request, requireSellerUid(session)));
    }

    private String getSellerScopeUid(HttpSession session) {
        String memberType = (String) session.getAttribute("loginMemberType");
        if ("SELLER".equals(memberType)) {
            String memberUid = (String) session.getAttribute("loginMember");
            if (memberUid == null || memberUid.isBlank()) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }
            return memberUid;
        }
        return null;
    }

    private String requireSellerUid(HttpSession session) {
        String memberType = (String) session.getAttribute("loginMemberType");
        if (!"SELLER".equals(memberType)) {
            throw new IllegalStateException("판매자만 처리할 수 있습니다.");
        }
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberUid;
    }
}
