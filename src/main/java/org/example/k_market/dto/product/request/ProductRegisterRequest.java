package org.example.k_market.dto.product.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegisterRequest {
    private Integer cateId;
    private String prodName;
    private String description;
    private String maker;
    private int price;
    private int discount;
    private int point;
    private int deliveryFee;

    private MultipartFile thumb1;
    private MultipartFile thumb2;
    private MultipartFile thumb3;
    private MultipartFile detailInfoFile;

    private String infoNoticeType;
    @Builder.Default
    private Map<String, String> informationNoticeValues = new LinkedHashMap<>();

    @Builder.Default
    private List<ProductOptionGroupRequest> optionGroups = new ArrayList<>();

    @Builder.Default
    private List<ProductVariantRequest> variants = new ArrayList<>();
}
