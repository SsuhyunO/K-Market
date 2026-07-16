package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySalesTotalDTO {
    private long cateId;
    private String cateName;
    private long totalAmount;
}
