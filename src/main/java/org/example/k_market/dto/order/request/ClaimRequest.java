package org.example.k_market.dto.order.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRequest {
    private String claimType;
    private String reasonType;
    private String claimContent;
    private Integer fileId;
}
