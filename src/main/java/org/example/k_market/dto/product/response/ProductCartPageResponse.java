package org.example.k_market.dto.product.response;

import org.example.k_market.dto.order.OrderItemViewDTO;

import java.util.List;

public record ProductCartPageResponse(
    List<OrderItemViewDTO> cartItems,
    int productTotal,
    int discountTotal,
    int shippingTotal,
    int earnPoint,
    int orderTotal,
    int finalPrice
) {
    public int itemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    public boolean isEmpty() {
        return itemCount() == 0;
    }
}
