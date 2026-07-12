package org.example.k_market.dto.product.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private int no;
    private String name;
    private String description;
    private int price; // 원가
    private int discount; // 할인율
    private int deliveryFee; // 배송비
    private Integer thumb1FileId;
    private String seller;
    private double rating;

    public int getSalePrice() {
        return (int)(price - ((double)price * discount / 100));
    }

    public boolean isFreeShipping() {
        return deliveryFee <= 0;
    }
}
