package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.product.Product;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private int prodNo;
    private int cateId;
    private String prodName;
    private int price;
    private int discount;
    private int point;
    private int sold;
    private int thumb1FileId;
    private int thumb2FileId;
    private int thumb3FileId;
    private String sellerUid;
    private String infoNoticeType;
    private String createAt;

    public Product toEntity() {
        return Product.builder()
                .prodNo(prodNo)
                .cateId(cateId)
                .prodName(prodName)
                .price(price)
                .discount(discount)
                .point(point)
                .sold(sold)
                .thumb1FileId(thumb1FileId)
                .thumb2FileId(thumb2FileId)
                .thumb3FileId(thumb3FileId)
                .sellerUid(sellerUid)
                .infoNoticeType(infoNoticeType)
                .build();
    }
}
