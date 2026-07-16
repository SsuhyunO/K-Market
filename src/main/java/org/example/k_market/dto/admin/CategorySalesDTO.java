package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySalesDTO {
    private String saleDate;      // '07-15' 형태
    private long cateId;          // 1차 카테고리 cateId
    private String cateName;      // 카테고리명
    private long amount;          // 그날 그 카테고리 매출 합계
}
