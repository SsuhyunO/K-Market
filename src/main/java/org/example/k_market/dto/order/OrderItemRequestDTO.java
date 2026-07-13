package org.example.k_market.dto.order;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    private Integer prodVariantId;
    private Integer count;
}
