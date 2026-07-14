package org.example.k_market.dto.order.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyOrderSearchRequest {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;
    private String startDate;
    private String endDate;
}
