package org.example.k_market.entity.category;

import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.category.CategoryDTO;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Category parent;

    private String code;
    private String name;
    private String infoNoticeType;
    private int sortOrder;

    public CategoryDTO toDTO() {
        return CategoryDTO.builder()
                .cateId(cateId)
                .parentId(parent != null ? parent.getCateId() : null)
                .code(code)
                .name(name)
                .infoNoticeType(infoNoticeType)
                .sortOrder(sortOrder)
                .build();
    }

    public void change(String name, String infoNoticeType, int sortOrder) {
        this.name = name;
        this.infoNoticeType = infoNoticeType;
        this.sortOrder = sortOrder;
    }
}
