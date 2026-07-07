package org.example.k_market.dto.category.request;

import lombok.*;
import org.example.k_market.dto.category.CategorySaveDTO;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveRequest {
    private List<CategorySaveDTO> categories;
}
