package org.example.k_market.dto.shop;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSearchRequest {
    /** DAILY(일별,기본값) / WEEKLY(주간) / MONTHLY(한달) */
    @Builder.Default
    private String period = "DAILY";

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}