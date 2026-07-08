package org.example.k_market.dto.pagination.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {
    @Builder.Default
    private int page = 1;
}
