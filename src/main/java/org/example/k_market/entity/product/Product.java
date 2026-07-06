package org.example.k_market.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.product.ProductDTO;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @CreationTimestamp
    private LocalDateTime createAt;

    public ProductDTO toDTO() {
        return ProductDTO.builder()
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
                .createAt(createAt.toString())
                .build();
    }
}
