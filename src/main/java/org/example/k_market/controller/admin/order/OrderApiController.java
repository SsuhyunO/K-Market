package org.example.k_market.controller.admin.order;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.order.request.DeliveryRegisterRequest;
import org.example.k_market.dto.order.request.ManagementOrderSearchRequest;
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
        @ModelAttribute ManagementOrderSearchRequest pageRequest
    ) {
        return ResponseEntity.ok(orderService.getOrderPageInfoForManagement(pageRequest));
    }

    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable int orderNo) {
        return ResponseEntity.ok(orderService.getOrderDetailForManagement(orderNo));
    }

    @GetMapping("/{orderNo}/shippable-items")
    public ResponseEntity<List<OrderLineResponse>> getShippableItems(@PathVariable int orderNo) {
        return ResponseEntity.ok(orderService.getShippableOrderLines(orderNo));
    }

    @PostMapping("/shipments")
    public ResponseEntity<ShipmentDetailResponse> registerDelivery(@RequestBody DeliveryRegisterRequest request) {
        return ResponseEntity.ok(orderService.registerDelivery(request));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<PageResponse<ShipmentListResponse>> getDeliveryPageInfo(
        @ModelAttribute ManagementOrderSearchRequest pageRequest
    ) {
        return ResponseEntity.ok(orderService.getShipmentPageInfo(pageRequest));
    }

    @GetMapping("/deliveries/{shipmentNo}")
    public ResponseEntity<ShipmentDetailResponse> getDeliveryDetail(@PathVariable int shipmentNo) {
        return ResponseEntity.ok(orderService.getShipmentDetail(shipmentNo));
    }
}
