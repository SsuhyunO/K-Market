package org.example.k_market.dto.category;

import lombok.*;
import org.example.k_market.entity.category.Category;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CategoryDTO {
    private int cateId;
    private Integer parentId;
    private String name;
    private String infoNoticeType;

    private String action; // CREATE, UPDATE, DELETE

    public Category toEntity() {
        Category parent = null;
        if (parentId != null) {
            parent = Category.builder()
                    .cateId(parentId)
                    .build();
        }

        return Category.builder()
                .cateId(cateId == 0 ? null : cateId)
                .parent(parent)
                .name(name)
                .infoNoticeType(infoNoticeType)
                .build();
    }
}
