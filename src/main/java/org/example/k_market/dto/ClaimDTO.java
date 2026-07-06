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
    private int orderNo;
    private String claimType;
    private String claimContent;
    private int fileId;
}
