package org.example.k_market.dto.order.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementOrderSearchRequest {
    @Builder.Default
    private int page = 1;
    private String type;
    private String keyword;
}
