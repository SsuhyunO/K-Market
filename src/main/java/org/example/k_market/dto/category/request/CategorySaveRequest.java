package org.example.k_market.dto.category.request;

import lombok.*;
import org.example.k_market.dto.category.CategoryDTO;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveRequest {
    private List<CategoryDTO> categories;
}
