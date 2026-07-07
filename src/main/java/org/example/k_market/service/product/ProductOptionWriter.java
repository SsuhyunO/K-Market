package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.product.request.ProductOptionGroupRequest;
import org.example.k_market.dto.product.request.ProductOptionItemRequest;
import org.example.k_market.dto.product.request.ProductVariantItemRequest;
import org.example.k_market.dto.product.request.ProductVariantRequest;
import org.example.k_market.entity.product.ProductOptionGroup;
import org.example.k_market.entity.product.ProductOptionItem;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.entity.product.ProductVariantItemId;
import org.example.k_market.repository.product.ProductOptionGroupRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductOptionWriter {
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;

    public void save(int prodNo, List<ProductOptionGroupRequest> groups, List<ProductVariantRequest> variants) {
        Map<String, Integer> optionItemIds = saveOptionGroups(prodNo, groups);
        saveVariants(prodNo, variants, optionItemIds);
    }

    private Map<String, Integer> saveOptionGroups(int prodNo, List<ProductOptionGroupRequest> groups) {
        Map<String, Integer> optionItemIds = new HashMap<>();

        if (groups == null || groups.isEmpty()) {
            return optionItemIds;
        }

        for (ProductOptionGroupRequest groupRequest : groups) {
            ProductOptionGroup group = optionGroupRepository.save(ProductOptionGroup.builder()
                .prodNo(prodNo)
                .name(groupRequest.getName())
                .deleted(false)
                .build());

            for (ProductOptionItemRequest itemRequest : groupRequest.getItems()) {
                ProductOptionItem item = optionItemRepository.save(ProductOptionItem.builder()
                    .groupId(group.getId())
                    .value(itemRequest.getValue())
                    .deleted(false)
                    .build());

                optionItemIds.put(itemRequest.getClientKey(), item.getId());
            }
        }

        return optionItemIds;
    }

    private void saveVariants(int prodNo, List<ProductVariantRequest> variants, Map<String, Integer> optionItemIds) {
        for (ProductVariantRequest variantRequest : variants) {
            ProductVariant variant = variantRepository.save(ProductVariant.builder()
                .prodNo(prodNo)
                .stock(variantRequest.getStock())
                .status(variantRequest.getStatus())
                .build());

            if (variantRequest.getItems() == null || variantRequest.getItems().isEmpty()) {
                continue;
            }

            for (ProductVariantItemRequest itemRequest : variantRequest.getItems()) {
                Integer optionItemId = optionItemIds.get(itemRequest.getItemKey());
                if (optionItemId == null) {
                    throw new IllegalArgumentException("상품 조합에 존재하지 않는 옵션 항목이 포함되어 있습니다.");
                }

                variantItemRepository.save(ProductVariantItem.builder()
                    .id(new ProductVariantItemId(variant.getId(), optionItemId))
                    .build());
            }
        }
    }
}
