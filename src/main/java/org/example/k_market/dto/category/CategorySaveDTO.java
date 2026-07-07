package org.example.k_market.dto.category;

import lombok.*;
import org.example.k_market.entity.category.Category;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveDTO {
    private String id;
    private String parentId;
    private String name;
    private String infoNoticeType;
    private int sortOrder;
    private String action;

    public Category toCategoryEntity(Category parent) {
        return Category.builder()
            .parent(parent)
            .name(name)
            .infoNoticeType(infoNoticeType)
            .sortOrder(sortOrder)
            .build();
    }
}
