package org.example.k_market.service.order;

import org.example.k_market.dto.order.OrderItemViewDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemTotalCalculator {

    public OrderItemTotals calculate(List<OrderItemViewDTO> items) {
        List<OrderItemViewDTO> safeItems = items == null ? List.of() : items;

        int productTotal = safeItems.stream()
            .mapToInt(item -> item.getPrice() * item.getQuantity())
            .sum();
        int discountTotal = safeItems.stream()
            .mapToInt(item -> (item.getPrice() * item.getDiscountRate() / 100) * item.getQuantity())
            .sum();
        int shippingTotal = safeItems.stream()
            .filter(item -> !item.isFreeShipping())
            .collect(Collectors.toMap(
                OrderItemViewDTO::getProdNo,
                OrderItemViewDTO::getShippingFee,
                (a, b) -> a,
                LinkedHashMap::new
            ))
            .values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        int earnPoint = safeItems.stream()
            .mapToInt(item -> item.getPoint() * item.getQuantity())
            .sum();

        return new OrderItemTotals(
            productTotal,
            discountTotal,
            shippingTotal,
            earnPoint,
            productTotal - discountTotal,
            productTotal - discountTotal + shippingTotal
        );
    }
}
