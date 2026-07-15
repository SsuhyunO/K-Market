package org.example.k_market.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private int reviewNo;
    private int orderItemNo;
    private String memberUid;
    private int prodNo;
    private int rating;
    private String content;
    private String createdAt;
    private Integer fileId;
}
