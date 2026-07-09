package org.example.k_market.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    private Integer prodVariantId;
    private Integer count;
}
