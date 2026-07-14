package org.example.k_market.dto.review.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListResponse {
    private int reviewNo;
    private int productNo;
    private String productName;
    private String memberUid;
    private String content;
    private int rating;
    private String createdAt;
}
