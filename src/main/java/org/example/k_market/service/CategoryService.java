package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryDTO;
import org.example.k_market.dto.category.request.CategorySaveRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.repository.category.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void saveCategories(CategorySaveRequest request) {
        for (CategoryDTO dto : request.getCategories()) {
            switch (dto.getAction()) {
                case "CREATE" -> categoryRepository.save(dto.toEntity());
                case "UPDATE" -> updateCategory(dto);
                case "DELETE" -> deleteCategory(dto.getCateId());
            }
        }
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(Category::toDTO)
                .toList();
    }

    private void updateCategory(CategoryDTO dto) {
        categoryRepository
                .findById(dto.getCateId())
                .ifPresent(category -> category.change(
                        dto.getName(),
                        dto.getInfoNoticeType()
                ));

    }

    private void deleteCategory(int cateId) {
        categoryRepository.deleteById(cateId);
    }
}
