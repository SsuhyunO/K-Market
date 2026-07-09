package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.repository.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    // TODO: product 쪽 완성되면 실제 Repository로 교체
    // private final ProductVariantRepository variantRepository;
    // private final ProductRepository productRepository;

    public List<OrderItemViewDTO> getOrderItemsDirect(Integer prodVariantId, Integer count) {
        // TODO: product 쪽 완성 전까지 임시 더미
        return List.of(OrderItemViewDTO.builder()
                .prodVariantId(prodVariantId)
                .productName("임시 상품명")
                .optionText("블랙 / L")
                .quantity(count)
                .price(89000)
                .discountRate(30)
                .point(623)
                .freeShipping(true)
                .shippingFee(0)
                .lineTotal(89000 * count * 70 / 100)
                .build());
    }

    public List<OrderItemViewDTO> getOrderItemsFromCart(List<Integer> cartNoList) {
        // TODO: cart 쪽도 prodVariantId 반영 필요, 우선 더미
        return List.of();
    }

    public int calcProductTotal(List<OrderItemViewDTO> items) {
        return items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
    }

    public int calcDiscountTotal(List<OrderItemViewDTO> items) {
        return items.stream()
                .mapToInt(i -> (i.getPrice() * i.getDiscountRate() / 100) * i.getQuantity())
                .sum();
    }

    public int calcShippingTotal(List<OrderItemViewDTO> items) {
        return items.stream()
                .filter(i -> !i.isFreeShipping())
                .mapToInt(OrderItemViewDTO::getShippingFee)
                .sum();
    }

    public int calcEarnPoint(List<OrderItemViewDTO> items) {
        return items.stream().mapToInt(i -> i.getPoint() * i.getQuantity()).sum();
    }
}
