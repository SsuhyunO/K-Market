package org.example.k_market.service.order;

public record OrderItemTotals(
    int productTotal,
    int discountTotal,
    int shippingTotal,
    int earnPoint,
    int orderTotal,
    int finalPrice
) {
}
