package org.example.k_market.service.order;

import org.example.k_market.dto.order.OrderItemViewDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTotalCalculatorTest {

    private final OrderItemTotalCalculator calculator = new OrderItemTotalCalculator();

    @Test
    void calculateDeduplicatesShippingFeeByProduct() {
        List<OrderItemViewDTO> items = List.of(
            item(100, 10_000, 1, 0, 3_000),
            item(100, 12_000, 1, 0, 3_000),
            item(200, 20_000, 1, 0, 2_500)
        );

        OrderItemTotals totals = calculator.calculate(items);

        assertThat(totals.shippingTotal()).isEqualTo(5_500);
        assertThat(totals.finalPrice()).isEqualTo(47_500);
    }

    private OrderItemViewDTO item(int prodNo, int price, int quantity, int discountRate, int shippingFee) {
        return OrderItemViewDTO.builder()
            .prodNo(prodNo)
            .price(price)
            .quantity(quantity)
            .discountRate(discountRate)
            .freeShipping(shippingFee <= 0)
            .shippingFee(shippingFee)
            .point(0)
            .build();
    }
}
