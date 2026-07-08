package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryDTO;
import org.example.k_market.dto.category.CategorySaveDTO;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.category.request.CategorySaveRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.repository.category.CategoryRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    /*
     * 카테고리 삭제 정책
     *
     * 1. 카테고리를 삭제하더라도 해당 카테고리로 분류되어 있던 상품은 삭제하지 않는다.
     *    상품 데이터는 유지하고, 상품의 카테고리만 시스템 미분류 카테고리로 이동한다.
     *
     * 2. 2차 카테고리를 삭제하는 경우에는 해당 2차 카테고리에 속한 상품만 미분류로 이동한 뒤
     *    대상 카테고리를 삭제한다.
     *
     * 3. 1차 카테고리를 삭제하는 경우에는 하위 2차 카테고리에 속한 상품을 모두 미분류로 이동한 뒤
     *    하위 2차 카테고리와 대상 1차 카테고리를 삭제한다.
     *
     * 4. 미분류 카테고리는 상품 보존을 위한 시스템 카테고리이므로 수정하거나 삭제할 수 없다.
     */
    private static final String UNCATEGORIZED_CODE = "UNCATEGORIZED";
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void saveCategories(CategorySaveRequest request) {
        List<CategorySaveDTO> categories = request.getCategories();

        validateActions(categories);

        Map<String, Category> createdCategoryMap = new HashMap<>();
        Map<String, List<CategorySaveDTO>> categoriesByAction = categories
            .stream()
            .collect(Collectors.groupingBy(CategorySaveDTO::getAction));

        // 1. 삭제
        categoriesByAction
            .getOrDefault("DELETE", List.of())
            .stream()
            .filter(dto -> isPersistedId(dto.getId()))
            .forEach(dto -> deleteCategory(toCateId(dto.getId())));

        Map<Boolean, List<CategorySaveDTO>> categoriesByHasParent = categoriesByAction
            .getOrDefault("CREATE", List.of())
            .stream()
            .collect(Collectors.partitioningBy(dto -> dto.getParentId() != null));

        // 2. 신규 루트 카테고리 생성
        categoriesByHasParent
            .get(false)
            .forEach(dto -> createdCategoryMap.put(dto.getId(), categoryRepository.save(dto.toCategoryEntity(null))));

        // 3. 신규 리프 카테고리 생성
        categoriesByHasParent
            .get(true)
            .forEach(dto -> createdCategoryMap.put(
                dto.getId(),
                categoryRepository.save(
                    dto.toCategoryEntity(
                        resolveParent(
                            dto.getParentId(),
                            createdCategoryMap)))));

        // 4. 기존 카테고리 수정
        categoriesByAction
            .getOrDefault("UPDATE", List.of())
            .stream()
            .filter(dto -> isPersistedId(dto.getId()))
            .forEach(this::updateCategory);
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository
                .findAllByOrderBySortOrderAscCateIdAsc()
                .stream()
                .filter(category -> !UNCATEGORIZED_CODE.equals(category.getCode()))
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

    private void updateCategory(CategorySaveDTO dto) {
        Category category = categoryRepository
                .findById(toCateId(dto.getId()))
                .orElseThrow();

        if (UNCATEGORIZED_CODE.equals(category.getCode())) {
            throw new IllegalArgumentException("미분류 카테고리는 수정할 수 없습니다.");
        }

        category.change(
            dto.getName(),
            dto.getInfoNoticeType(),
            dto.getSortOrder()
        );
    }

    private int toCateId(String id) {
        if (!isPersistedId(id)) {
            throw new IllegalArgumentException("DB 카테고리 ID가 아닙니다: " + id);
        }

        return Integer.parseInt(id);
    }

    private boolean isPersistedId(String id) {
        return id != null && id.matches("[1-9]\\d*");
    }

    private void validateActions(List<CategorySaveDTO> categories) {
        for (CategorySaveDTO dto : categories) {
            if (dto.getAction() == null) {
                throw new IllegalArgumentException("카테고리 작업 유형이 없습니다.");
            }

            if (!isSupportedAction(dto.getAction())) {
                throw new IllegalArgumentException("지원하지 않는 카테고리 작업입니다.: " + dto.getAction());
            }
        }
    }

    private Category resolveParent(String parentId, Map<String, Category> createdCategoryMap) {
        if (parentId == null) {
            return null;
        }

        Category parent = createdCategoryMap.get(parentId);

        if (parent != null) {
            return parent;
        }

        if (isPersistedId(parentId)) {
            return categoryRepository.findById(toCateId(parentId))
                .orElseThrow(() -> new IllegalArgumentException("부모 카테고리가 존재하지 않습니다. id=" + parentId));
        }

        throw new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다. id=" + parentId);
    }

    private boolean isSupportedAction(String action) {
        return "CREATE".equals(action) || "UPDATE".equals(action) || "DELETE".equals(action);
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
