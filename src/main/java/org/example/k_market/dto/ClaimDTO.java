package org.example.k_market.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimDTO {
    private int claimNo;
    private int orderItemNo;
    private String claimType;
    private String claimContent;
    private Integer fileId;
    private int quantity;
    private String claimStatus;
    private String requestedAt;
    private String processedAt;
}
