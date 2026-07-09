package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.common.CategoryCodes;
import org.example.k_market.common.product.ProductInfoNoticeField;
import org.example.k_market.common.product.ProductInfoNoticeTemplate;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dto.product.request.ProductOptionGroupRequest;
import org.example.k_market.dto.product.request.ProductOptionItemRequest;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.dto.product.request.ProductVariantItemRequest;
import org.example.k_market.dto.product.request.ProductVariantRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.repository.SellerRepository;
import org.example.k_market.repository.category.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProductFormValidator {
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    public Category validateForSave(ProductRegisterRequest request, String sellerUid) {
        Category category = categoryRepository
            .findById(request.getCateId())
            .orElseThrow();

        validateSeller(sellerUid);
        validateCategory(category, request);
        validateProductInfo(request);
        validateNoticeValues(request);
        validateOptionVariants(request.getOptionGroups(), request.getVariants());

        return category;
    }

    private void validateSeller(String sellerUid) {
        if (isBlank(sellerUid)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        if (!sellerRepository.existsById(sellerUid)) {
            throw new IllegalStateException("판매자만 상품을 등록할 수 있습니다.");
        }
    }

    private void validateCategory(Category category, ProductRegisterRequest request) {
        if (category.getParent() == null) {
            throw new IllegalStateException("선택된 카테고리가 2차 카테고리가 아닙니다.");
        }

        if (CategoryCodes.UNCATEGORIZED.equals(category.getCode())) {
            throw new IllegalStateException("미분류 카테고리에는 상품을 등록할 수 없습니다.");
        }

        if (!category.getInfoNoticeType().equals(request.getInfoNoticeType())) {
            throw new IllegalStateException("카테고리와 상품의 상품군이 일치하지 않습니다.");
        }
    }

    private void validateProductInfo(ProductRegisterRequest request) {
        if (isBlank(request.getProdName()) ||
            request.getPoint() < 0 ||
            request.getDiscount() < 0 ||
            request.getDiscount() > 100 ||
            request.getPrice() < 0 ||
            isBlank(request.getDescription()) ||
            isBlank(request.getMaker()) ||
            request.getDeliveryFee() < 0) {
            throw new IllegalArgumentException("상품정보가 유효하지 않습니다.");
        }
    }

    private void validateNoticeValues(ProductRegisterRequest request) {
        ProductInfoNoticeTemplate template = ProductInfoNoticeTemplates.all().get(request.getInfoNoticeType());
        if (template == null) {
            throw new IllegalArgumentException("지원하지 않는 상품정보 제공고시 상품군입니다.");
        }

        Map<String, String> noticeValues = request.getInformationNoticeValues();
        if (noticeValues == null) {
            throw new IllegalArgumentException("상품정보 제공고시 항목을 입력해주세요.");
        }

        Set<String> allowedKeys = new HashSet<>();
        for (ProductInfoNoticeField field : template.fields()) {
            allowedKeys.add(field.key());

            if (field.required() && isBlank(noticeValues.get(field.key()))) {
                throw new IllegalArgumentException("필수 상품정보 제공고시 항목을 입력해주세요: " + field.label());
            }
        }

        for (String noticeKey : noticeValues.keySet()) {
            if (!allowedKeys.contains(noticeKey)) {
                throw new IllegalArgumentException("허용되지 않은 상품정보 제공고시 항목입니다: " + noticeKey);
            }
        }
    }

    private void validateOptionVariants(List<ProductOptionGroupRequest> groups, List<ProductVariantRequest> variants) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("상품 조합 정보가 필요합니다.");
        }

        if (groups == null || groups.isEmpty()) {
            validateDefaultVariant(variants);
            return;
        }

        Map<String, Set<String>> itemKeysByGroupKey = new HashMap<>();
        Set<String> groupKeys = new HashSet<>();
        Set<String> groupNames = new HashSet<>();
        Set<String> allItemKeys = new HashSet<>();

        for (ProductOptionGroupRequest group : groups) {
            if (isBlank(group.getClientKey())) {
                throw new IllegalArgumentException("옵션 그룹 식별자가 없습니다.");
            }
            if (!groupKeys.add(group.getClientKey())) {
                throw new IllegalArgumentException("중복된 옵션 그룹 식별자가 있습니다.");
            }
            if (isBlank(group.getName())) {
                throw new IllegalArgumentException("옵션 이름을 입력해주세요.");
            }
            if (!groupNames.add(group.getName().trim())) {
                throw new IllegalArgumentException("중복된 옵션 이름은 사용할 수 없습니다.");
            }
            if (group.getItems() == null || group.getItems().isEmpty()) {
                throw new IllegalArgumentException("옵션 항목은 1개 이상 필요합니다.");
            }

            Set<String> groupItemKeys = new HashSet<>();
            Set<String> itemValues = new HashSet<>();
            for (ProductOptionItemRequest item : group.getItems()) {
                if (isBlank(item.getClientKey())) {
                    throw new IllegalArgumentException("옵션 항목 식별자가 없습니다.");
                }
                if (!allItemKeys.add(item.getClientKey())) {
                    throw new IllegalArgumentException("중복된 옵션 항목 식별자가 있습니다.");
                }
                if (isBlank(item.getValue())) {
                    throw new IllegalArgumentException("빈 옵션 항목은 사용할 수 없습니다.");
                }
                if (!itemValues.add(item.getValue().trim())) {
                    throw new IllegalArgumentException("중복된 옵션 항목은 사용할 수 없습니다.");
                }
                groupItemKeys.add(item.getClientKey());
            }

            itemKeysByGroupKey.put(group.getClientKey(), groupItemKeys);
        }

        Set<String> variantKeys = new HashSet<>();
        for (ProductVariantRequest variant : variants) {
            validateVariantBase(variant);

            if (variant.getItems() == null || variant.getItems().size() != groups.size()) {
                throw new IllegalArgumentException("상품 조합 항목 수가 옵션 그룹 수와 일치하지 않습니다.");
            }

            Set<String> usedGroupKeys = new HashSet<>();
            Set<String> combinationItemKeys = new HashSet<>();

            for (ProductVariantItemRequest item : variant.getItems()) {
                if (isBlank(item.getGroupKey()) || isBlank(item.getItemKey())) {
                    throw new IllegalArgumentException("상품 조합 항목 정보가 올바르지 않습니다.");
                }
                if (!usedGroupKeys.add(item.getGroupKey())) {
                    throw new IllegalArgumentException("하나의 조합에 같은 옵션 그룹이 중복 포함되어 있습니다.");
                }

                Set<String> validItemKeys = itemKeysByGroupKey.get(item.getGroupKey());
                if (validItemKeys == null || !validItemKeys.contains(item.getItemKey())) {
                    throw new IllegalArgumentException("상품 조합에 존재하지 않는 옵션 항목이 포함되어 있습니다.");
                }

                combinationItemKeys.add(item.getItemKey());
            }

            String variantKey = String.join("|", combinationItemKeys.stream().sorted().toList());
            if (!variantKeys.add(variantKey)) {
                throw new IllegalArgumentException("중복된 상품 조합은 사용할 수 없습니다.");
            }
        }
    }

    private void validateDefaultVariant(List<ProductVariantRequest> variants) {
        if (variants.size() != 1) {
            throw new IllegalArgumentException("옵션이 없는 상품은 기본 조합 1개만 등록할 수 있습니다.");
        }

        ProductVariantRequest variant = variants.get(0);
        validateVariantBase(variant);

        if (variant.getItems() != null && !variant.getItems().isEmpty()) {
            throw new IllegalArgumentException("옵션이 없는 상품 조합에는 옵션 항목을 포함할 수 없습니다.");
        }
    }

    private void validateVariantBase(ProductVariantRequest variant) {
        if (variant.getStock() < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
        if (!Set.of("ON_SALE", "SOLD_OUT", "STOPPED").contains(variant.getStatus())) {
            throw new IllegalArgumentException("지원하지 않는 판매상태입니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
