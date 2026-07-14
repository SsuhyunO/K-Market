package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductCommonNoticeKeys;
import org.example.k_market.dto.product.request.ProductOptionGroupRequest;
import org.example.k_market.dto.product.request.ProductOptionItemRequest;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.dto.product.request.ProductVariantItemRequest;
import org.example.k_market.dto.product.request.ProductVariantRequest;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.entity.category.Category;
import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductNoticeValueId;
import org.example.k_market.entity.product.ProductOptionGroup;
import org.example.k_market.entity.product.ProductOptionItem;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.entity.product.ProductVariantItemId;
import org.example.k_market.repository.cart.CartRepository;
import org.example.k_market.repository.order.OrderItemRepository;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.example.k_market.repository.product.ProductOptionGroupRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductModifier {
    private static final String DELETED_VARIANT_STATUS = "DELETED";

    private final ProductRepository productRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final ProductNoticeValueRepository noticeValueRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductFormValidator productFormValidator;
    private final ProductFileUploader productFileUploader;
    private final ProductDetailViewer productDetailViewer;

    public void modify(int prodNo, ProductRegisterRequest request) {
        Category category = productFormValidator.validateForModify(request);

        ProductDetailResponse current = productDetailViewer.getProductDetailForManagement(prodNo);
        ProductUploadedFiles uploadedFiles = productFileUploader.uploadOptionalFiles(
            request,
            new ProductUploadedFiles(
                current.getThumb1FileId(),
                current.getThumb2FileId(),
                current.getThumb3FileId(),
                current.getDetailInfoFileId()
            )
        );

        productRepository.updateProductForModify(
            prodNo,
            category,
            request.getProdName(),
            request.getDescription(),
            request.getMaker(),
            request.getDeliveryFee(),
            request.getPrice(),
            request.getDiscount(),
            request.getPoint(),
            request.getTaxType(),
            request.getReceiptIssueType(),
            request.getBusinessType(),
            request.getBrand(),
            request.getOrigin(),
            uploadedFiles.thumb1FileId(),
            uploadedFiles.thumb2FileId(),
            uploadedFiles.thumb3FileId(),
            uploadedFiles.detailInfoFileId(),
            request.getInfoNoticeType()
        );

        Map<String, Integer> optionItemIdsByClientKey = upsertOptionGroupsAndItems(prodNo, request.getOptionGroups());
        VariantDeletionPlan variantDeletionPlan = upsertVariants(prodNo, request.getVariants(), optionItemIdsByClientKey);
        applyRemovedOptionPolicy(prodNo, request.getOptionGroups(), variantDeletionPlan);
        replaceNoticeValues(prodNo, request);
    }

    private Map<String, Integer> upsertOptionGroupsAndItems(int prodNo, List<ProductOptionGroupRequest> groups) {
        Map<String, Integer> itemIdsByClientKey = new HashMap<>();

        for (ProductOptionGroupRequest groupRequest : groups) {
            int groupId = resolveGroupId(prodNo, groupRequest);

            for (ProductOptionItemRequest itemRequest : groupRequest.getItems()) {
                int itemId = resolveItemId(groupId, itemRequest);
                itemIdsByClientKey.put(itemRequest.getClientKey(), itemId);
            }
        }

        return itemIdsByClientKey;
    }

    private int resolveGroupId(int prodNo, ProductOptionGroupRequest request) {
        if (request.getId() != null) {
            int updated = optionGroupRepository.updateNameByIdAndProdNo(request.getId(), prodNo, request.getName());
            if (updated == 0) {
                throw new IllegalArgumentException("존재하지 않는 옵션 그룹입니다.");
            }
            return request.getId();
        }

        int groupId = optionGroupRepository.save(ProductOptionGroup.builder()
            .prodNo(prodNo)
            .name(request.getName())
            .deleted(false)
            .build()).getId();
        request.setId(groupId);
        return groupId;
    }

    private int resolveItemId(int groupId, ProductOptionItemRequest request) {
        if (request.getId() != null) {
            int updated = optionItemRepository.updateValueByIdAndGroupId(request.getId(), groupId, request.getValue());
            if (updated == 0) {
                throw new IllegalArgumentException("존재하지 않는 옵션 항목입니다.");
            }
            return request.getId();
        }

        int itemId = optionItemRepository.save(ProductOptionItem.builder()
            .groupId(groupId)
            .value(request.getValue())
            .deleted(false)
            .build()).getId();
        request.setId(itemId);
        return itemId;
    }

    private VariantDeletionPlan upsertVariants(
        int prodNo,
        List<ProductVariantRequest> variants,
        Map<String, Integer> optionItemIdsByClientKey
    ) {
        Map<Integer, ProductVariant> existingVariants = variantRepository.findByProdNoAndStatusNotOrderByIdAsc(prodNo, DELETED_VARIANT_STATUS)
            .stream()
            .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));
        Set<Integer> submittedVariantIds = new HashSet<>();

        for (ProductVariantRequest request : variants) {
            int variantId = resolveVariantId(prodNo, request, existingVariants);
            submittedVariantIds.add(variantId);
            replaceVariantItems(variantId, request.getItems(), optionItemIdsByClientKey);
        }

        List<Integer> removedVariantIds = existingVariants.keySet().stream()
            .filter(id -> !submittedVariantIds.contains(id))
            .toList();

        return applyRemovedVariantPolicy(prodNo, removedVariantIds);
    }

    private int resolveVariantId(int prodNo, ProductVariantRequest request, Map<Integer, ProductVariant> existingVariants) {
        if (request.getId() != null) {
            if (!existingVariants.containsKey(request.getId())) {
                throw new IllegalArgumentException("존재하지 않는 상품 조합입니다.");
            }

            variantRepository.updateByIdAndProdNo(request.getId(), prodNo, request.getStock(), request.getStatus());
            return request.getId();
        }

        int variantId = variantRepository.save(ProductVariant.builder()
            .prodNo(prodNo)
            .stock(request.getStock())
            .status(request.getStatus())
            .build()).getId();
        request.setId(variantId);
        return variantId;
    }

    private void replaceVariantItems(
        int variantId,
        List<ProductVariantItemRequest> items,
        Map<String, Integer> optionItemIdsByClientKey
    ) {
        Set<Integer> submittedOptionItemIds = resolveVariantOptionItemIds(items, optionItemIdsByClientKey);
        Set<Integer> existingOptionItemIds = variantItemRepository.findByIdVariantIdIn(List.of(variantId)).stream()
            .map(item -> item.getId().getOptionItemId())
            .collect(Collectors.toSet());

        List<Integer> removedOptionItemIds = existingOptionItemIds.stream()
            .filter(id -> !submittedOptionItemIds.contains(id))
            .toList();
        List<Integer> addedOptionItemIds = submittedOptionItemIds.stream()
            .filter(id -> !existingOptionItemIds.contains(id))
            .toList();

        if (!removedOptionItemIds.isEmpty()) {
            variantItemRepository.deleteByVariantIdAndOptionItemIdIn(variantId, removedOptionItemIds);
        }

        addedOptionItemIds
            .forEach(id -> variantItemRepository.save(ProductVariantItem.builder()
                .id(new ProductVariantItemId(variantId, id))
                .build()));
    }

    private Set<Integer> resolveVariantOptionItemIds(
        List<ProductVariantItemRequest> items,
        Map<String, Integer> optionItemIdsByClientKey
    ) {
        Set<Integer> optionItemIds = new HashSet<>();

        for (ProductVariantItemRequest itemRequest : items) {
            Integer optionItemId = optionItemIdsByClientKey.get(itemRequest.getItemKey());
            if (optionItemId == null) {
                throw new IllegalArgumentException("상품 조합에 존재하지 않는 옵션 항목이 포함되어 있습니다.");
            }

            optionItemIds.add(optionItemId);
        }

        return optionItemIds;
    }

    private VariantDeletionPlan applyRemovedVariantPolicy(int prodNo, List<Integer> removedVariantIds) {
        if (removedVariantIds.isEmpty()) {
            return new VariantDeletionPlan(Set.of(), Set.of());
        }

        cartRepository.deleteByProdVariantIdIn(removedVariantIds);

        Set<Integer> softDeleteVariantIds = new HashSet<>(orderItemRepository.findReferencedVariantIds(removedVariantIds));
        List<Integer> hardDeleteVariantIds = removedVariantIds.stream()
            .filter(id -> !softDeleteVariantIds.contains(id))
            .toList();

        Set<Integer> softDeleteOptionItemIds = findVariantOptionItemIds(softDeleteVariantIds);

        if (!softDeleteVariantIds.isEmpty()) {
            variantRepository.updateStatusByIdIn(softDeleteVariantIds.stream().toList(), DELETED_VARIANT_STATUS);
        }

        if (!hardDeleteVariantIds.isEmpty()) {
            variantItemRepository.deleteAllByVariantIdIn(hardDeleteVariantIds);
            variantRepository.deleteAllByIdInBatch(hardDeleteVariantIds);
        }

        return new VariantDeletionPlan(softDeleteVariantIds, softDeleteOptionItemIds);
    }

    private Set<Integer> findVariantOptionItemIds(Set<Integer> variantIds) {
        if (variantIds.isEmpty()) {
            return Set.of();
        }

        return variantItemRepository.findByIdVariantIdIn(variantIds.stream().toList()).stream()
            .map(item -> item.getId().getOptionItemId())
            .collect(Collectors.toSet());
    }

    private void applyRemovedOptionPolicy(
        int prodNo,
        List<ProductOptionGroupRequest> submittedGroups,
        VariantDeletionPlan variantDeletionPlan
    ) {
        List<ProductOptionGroup> existingGroups = optionGroupRepository.findByProdNoAndDeletedFalseOrderByIdAsc(prodNo);
        List<Integer> existingGroupIds = existingGroups.stream()
            .map(ProductOptionGroup::getId)
            .toList();
        Map<Integer, ProductOptionItem> existingItemsById = existingGroupIds.isEmpty()
            ? Map.of()
            : optionItemRepository.findByGroupIdInAndDeletedFalseOrderByIdAsc(existingGroupIds).stream()
                .collect(Collectors.toMap(ProductOptionItem::getId, Function.identity()));
        Set<Integer> submittedGroupIds = submittedGroups.stream()
            .map(ProductOptionGroupRequest::getId)
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toSet());
        Set<Integer> submittedItemIds = submittedGroups.stream()
            .flatMap(group -> group.getItems().stream())
            .map(ProductOptionItemRequest::getId)
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toSet());
        List<Integer> removedGroupIds = existingGroupIds.stream()
            .filter(id -> !submittedGroupIds.contains(id))
            .toList();
        List<Integer> removedItemIds = existingItemsById.keySet().stream()
            .filter(id -> !submittedItemIds.contains(id))
            .toList();

        if (removedGroupIds.isEmpty() && removedItemIds.isEmpty()) {
            return;
        }

        Set<Integer> softDeleteItemIds = removedItemIds.stream()
            .filter(variantDeletionPlan.softDeleteOptionItemIds()::contains)
            .collect(Collectors.toSet());
        Set<Integer> softDeleteGroupIds = findSoftDeleteGroupIds(removedGroupIds, softDeleteItemIds, existingItemsById);
        List<Integer> hardDeleteItemIds = removedItemIds.stream()
            .filter(id -> !softDeleteItemIds.contains(id))
            .toList();
        List<Integer> hardDeleteGroupIds = removedGroupIds.stream()
            .filter(id -> !softDeleteGroupIds.contains(id))
            .toList();

        if (!softDeleteItemIds.isEmpty()) {
            optionItemRepository.markDeletedByIdIn(softDeleteItemIds.stream().toList());
        }
        if (!softDeleteGroupIds.isEmpty()) {
            optionGroupRepository.markDeletedByIdIn(softDeleteGroupIds.stream().toList());
        }
        if (!hardDeleteItemIds.isEmpty()) {
            optionItemRepository.deleteAllByIdInBatch(hardDeleteItemIds);
        }
        if (!hardDeleteGroupIds.isEmpty()) {
            optionGroupRepository.deleteAllByIdInBatch(hardDeleteGroupIds);
        }
    }

    private Set<Integer> findSoftDeleteGroupIds(
        List<Integer> removedGroupIds,
        Set<Integer> softDeleteItemIds,
        Map<Integer, ProductOptionItem> existingItemsById
    ) {
        Map<Integer, List<ProductOptionItem>> itemsByGroupId = existingItemsById.values().stream()
            .collect(Collectors.groupingBy(ProductOptionItem::getGroupId, LinkedHashMap::new, Collectors.toList()));

        return removedGroupIds.stream()
            .filter(groupId -> itemsByGroupId.getOrDefault(groupId, List.of()).stream()
                .map(ProductOptionItem::getId)
                .anyMatch(softDeleteItemIds::contains))
            .collect(Collectors.toSet());
    }

    private void replaceNoticeValues(int prodNo, ProductRegisterRequest request) {
        Map<String, String> submittedValues = request.getInformationNoticeValues().entrySet().stream()
            .filter(entry -> !ProductCommonNoticeKeys.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (left, right) -> right, LinkedHashMap::new));
        Map<String, ProductNoticeValue> existingValuesByKey = noticeValueRepository.findByIdProdNo(prodNo).stream()
            .collect(Collectors.toMap(value -> value.getId().getNoticeKey(), Function.identity()));

        List<String> removedKeys = existingValuesByKey.keySet().stream()
            .filter(key -> !submittedValues.containsKey(key))
            .toList();

        if (!removedKeys.isEmpty()) {
            noticeValueRepository.deleteByProdNoAndNoticeKeyIn(prodNo, removedKeys);
        }

        submittedValues.forEach((noticeKey, value) -> {
            ProductNoticeValue existingValue = existingValuesByKey.get(noticeKey);
            if (existingValue != null) {
                existingValue.updateValue(value);
                return;
            }

            noticeValueRepository.save(ProductNoticeValue.builder()
                .id(new ProductNoticeValueId(noticeKey, prodNo))
                .value(value)
                .build());
        });
    }

    private record VariantDeletionPlan(
        Set<Integer> softDeleteVariantIds,
        Set<Integer> softDeleteOptionItemIds
    ) {
    }
}
