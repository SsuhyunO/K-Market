package org.example.k_market.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.product.ProductDTO;
import org.example.k_market.entity.category.Category;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    private static final String DEFAULT_STATUS = "ON_SALE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int prodNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cateId")
    private Category category;
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
    private Integer thumb1FileId;
    private Integer thumb2FileId;
    private Integer thumb3FileId;
    private Integer detailInfoFileId;
    private String sellerUid;
    private String infoNoticeType;
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (status == null || status.isBlank()) {
            status = DEFAULT_STATUS;
        }
    }

    public ProductDTO toDTO() {
        return ProductDTO.builder()
            .prodNo(prodNo)
            .cateId(category.getCateId())
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
            .createdAt(createdAt.toString())
            .build();
    }
}
