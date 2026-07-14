package org.example.k_market.dto.order.response;

import lombok.*;
import org.example.k_market.dto.order.OrderDTO;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private OrderDTO order;
    private List<OrderLineResponse> items;
}
