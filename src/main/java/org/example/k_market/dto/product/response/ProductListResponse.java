package org.example.k_market.dto.product.response;

import lombok.*;
import org.example.k_market.enums.seller.SellerGrade;

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
    private String sellerUid;

    private double rating;
    private SellerGrade sellerGrade;

    public int getSalePrice() {
        return (int)(price - ((double)price * discount / 100));
    }

    public String getThumbnailUrl() {
        return thumb1FileId != null
            ? "/files/" + thumb1FileId
            : null;
    }

    public boolean isFreeShipping() {
        return deliveryFee <= 0;
    }

    public String getRatingStars() {
        int star = (int)Math.round(rating);
        return "★".repeat(star)
            + "☆".repeat(5 - star);
    }
}
