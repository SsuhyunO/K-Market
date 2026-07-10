package org.example.k_market.dto.product.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagementProductListResponse {
    private int prodNo;
    private String prodName;
    private int price;
    private int discount;
    private int point;
    private int sold;
    private int stock;
    private Integer thumb1FileId;
    private String sellerUid;
}
