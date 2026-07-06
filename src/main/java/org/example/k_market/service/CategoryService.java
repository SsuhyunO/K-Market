package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryDTO;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.category.request.CategorySaveRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.repository.category.CategoryRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final String UNCATEGORIZED_CODE = "UNCATEGORIZED";
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void saveCategories(CategorySaveRequest request) {
        for (CategoryDTO dto : request.getCategories()) {
            if (dto.getAction() == null) {
                throw new IllegalArgumentException("카테고리 작업 유형이 없습니다.");
            }

            switch (dto.getAction()) {
                case "CREATE" -> categoryRepository.save(dto.toEntity());
                case "UPDATE" -> updateCategory(dto);
                case "DELETE" -> deleteCategory(dto.getCateId());
                default -> throw new IllegalArgumentException("지원하지 않는 카테고리 작업입니다.: " + dto.getAction());
            }
        }
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository
                .findAllByOrderBySortOrderAscCateIdAsc()
                .stream()
                .map(Category::toDTO)
                .toList();
    }

    public List<CategoryTreeDTO> getCategoryTree() {
        List<CategoryDTO> categories = getCategories();

        Map<Integer, List<CategoryDTO>> childrenByParentId = categories
                .stream()
                .filter(category -> category.getParentId() != null) // .. 2차 카테고리 찾기
                .collect(Collectors.groupingBy(CategoryDTO::getParentId));

        return categories
                .stream()
                .filter(category -> category.getParentId() == null) // .. 루트 카테고리 찾기
                .map(root -> CategoryTreeDTO.builder()
                        .cateId(root.getCateId())
                        .name(root.getName())
                        .infoNoticeType(root.getInfoNoticeType())
                        .children(childrenByParentId.getOrDefault(root.getCateId(), List.of())) // 루트 카테고리에 자식 카테고리 초기화
                        .build())
                .toList();
    }

    public List<CategoryDTO> getRootCategories() {
        return categoryRepository
                .findByParentIsNull()
                .stream()
                .map(Category::toDTO)
                .toList();
    }

    public List<CategoryDTO> getSubCategories(int parentId) {
        return categoryRepository
                .findByParent_CateId(parentId)
                .stream()
                .map(Category::toDTO)
                .toList();
    }

    public String getInfoNoticeType(int cateId) {
        return categoryRepository
                .findById(cateId)
                .map(Category::getInfoNoticeType)
                .orElse(null);
    }

    public CategoryDTO getCategory(int cateId) {
        return categoryRepository
                .findById(cateId)
                .map(Category::toDTO)
                .orElse(null);
    }

    private void updateCategory(CategoryDTO dto) {
        categoryRepository
                .findById(dto.getCateId())
                .ifPresent(category -> category.change(
                        dto.getName(),
                        dto.getInfoNoticeType()
                ));

        category.change(
            dto.getName(),
            dto.getInfoNoticeType(),
            dto.getSortOrder()
        );
    }

    private void deleteCategory(int cateId) {
        Category target = categoryRepository
                .findById(cateId)
                .orElseThrow();

        Category uncategorized = categoryRepository.findByCode(UNCATEGORIZED_CODE);

        if (uncategorized == null) {
            throw new IllegalStateException("미분류 카테고리가 없습니다.");
        }

        if (target.getCateId().equals(uncategorized.getCateId())) {
            throw new IllegalArgumentException("미분류 카테고리는 삭제할 수 없습니다.");
        }

        if (target.getParent() != null) {
            // 2차 카테고리 삭제: 자기 자신에 속한 상품을 미분류로 이동
            productRepository.moveProductsToUncategorized(
                    List.of(target.getCateId()),
                    uncategorized
            );

            categoryRepository.deleteById(target.getCateId());
        } else {
            // 1차 카테고리 삭제: 하위 2차 카테고리 상품을 미분류로 이동 후 자식 삭제
            List<Integer> childCategoryIds = categoryRepository
                    .findByParent_CateId(target.getCateId())
                    .stream()
                    .map(Category::getCateId)
                    .toList();

            if (!childCategoryIds.isEmpty()) {
                productRepository.moveProductsToUncategorized(childCategoryIds, uncategorized);
                categoryRepository.deleteAllById(childCategoryIds);
            }

            categoryRepository.deleteById(target.getCateId());
        }
    }
}
