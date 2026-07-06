package org.example.k_market.dto.category;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeDTO {
    private int cateId;
    private String name;
    private String infoNoticeType;
    private List<CategoryDTO> children;
}
