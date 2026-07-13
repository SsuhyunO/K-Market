package org.example.k_market.dto.product;

import lombok.*;
import org.example.k_market.entity.category.Category;
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
    private String description;
    private String maker;
    private int deliveryFee;
    private int price;
    private int discount;
    private int point;
    private int hit;
    private String taxType;
    private String receiptIssueType;
    private String businessType;
    private String brand;
    private String origin;
    private int thumb1FileId;
    private int thumb2FileId;
    private int thumb3FileId;
    private int detailInfoFileId;
    private String sellerUid;
    private String infoNoticeType;
    private String createdAt;

    public Product toEntity(Category category) {
        return Product.builder()
            .prodNo(prodNo)
            .category(category)
            .prodName(prodName)
            .description(description)
            .maker(maker)
            .deliveryFee(deliveryFee)
            .price(price)
            .discount(discount)
            .point(point)
            .hit(hit)
            .taxType(taxType)
            .receiptIssueType(receiptIssueType)
            .businessType(businessType)
            .brand(brand)
            .origin(origin)
            .thumb1FileId(thumb1FileId)
            .thumb2FileId(thumb2FileId)
            .thumb3FileId(thumb3FileId)
            .detailInfoFileId(detailInfoFileId)
            .sellerUid(sellerUid)
            .infoNoticeType(infoNoticeType)
            .build();
    }
}
