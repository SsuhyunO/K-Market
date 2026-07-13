package org.example.k_market.dto.seller.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerStatisticsResponse {
    private String sellerUid;
    private double averageRating;
    private int reviewCount;
    private int salesCount;
}
