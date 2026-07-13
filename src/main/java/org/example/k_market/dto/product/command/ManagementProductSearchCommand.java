package org.example.k_market.dto.product.command;

import lombok.*;
import org.example.k_market.dto.product.request.ManagementProductSearchRequest;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementProductSearchCommand {
    private ManagementProductSearchRequest request;
    private String sellerUid;
    private String role;
}
