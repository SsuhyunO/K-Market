package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductInfoNoticeField;
import org.example.k_market.common.product.ProductInfoNoticeTemplate;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dao.ProductDAO;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.entity.admin.File;
import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductOptionGroup;
import org.example.k_market.entity.product.ProductOptionItem;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.repository.admin.FileRepository;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.example.k_market.repository.product.ProductOptionGroupRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductDetailViewer {
    private final ProductDAO productDAO;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final ProductNoticeValueRepository noticeValueRepository;
    private final FileRepository fileRepository;

    public ProductDetailResponse getProductDetail(int prodNo) {
        ProductDetailResponse detail = productDAO.findProductDetail(prodNo);
        if (detail == null) {
            throw new NoSuchElementException("존재하지 않는 상품입니다.");
        }

        fillProductDetailOptions(detail, prodNo);
        fillProductDetailNoticeValues(detail, prodNo);
        fillProductDetailFiles(detail);
        return detail;
    }

    private void fillProductDetailFiles(ProductDetailResponse detail) {
        List<Integer> fileIds = Stream.of(
                detail.getThumb1FileId(),
                detail.getThumb2FileId(),
                detail.getThumb3FileId(),
                detail.getDetailInfoFileId()
            )
            .filter(id -> id != null && id > 0)
            .distinct()
            .toList();

        if (fileIds.isEmpty()) {
            return;
        }

        Map<Integer, ProductDetailResponse.FileInfo> filesById = fileRepository.findByIdIn(fileIds).stream()
            .collect(Collectors.toMap(File::getId, this::toDetailFileInfo));

        detail.setThumb1File(filesById.get(detail.getThumb1FileId()));
        detail.setThumb2File(filesById.get(detail.getThumb2FileId()));
        detail.setThumb3File(filesById.get(detail.getThumb3FileId()));
        detail.setDetailInfoFile(filesById.get(detail.getDetailInfoFileId()));
    }

    private ProductDetailResponse.FileInfo toDetailFileInfo(File file) {
        return ProductDetailResponse.FileInfo.builder()
            .id(file.getId())
            .originalName(file.getOriginalName())
            .extension(file.getExtension())
            .fileSize(file.getFileSize())
            .build();
    }

    private void fillProductDetailOptions(ProductDetailResponse detail, int prodNo) {
        List<ProductOptionGroup> groups = optionGroupRepository.findByProdNoAndDeletedFalseOrderByIdAsc(prodNo);
        List<Integer> groupIds = groups.stream()
            .map(ProductOptionGroup::getId)
            .toList();
        List<ProductOptionItem> items = groupIds.isEmpty()
            ? Collections.emptyList()
            : optionItemRepository.findByGroupIdInAndDeletedFalseOrderByIdAsc(groupIds);

        Map<Integer, List<ProductOptionItem>> itemsByGroupId = items.stream()
            .collect(Collectors.groupingBy(ProductOptionItem::getGroupId, LinkedHashMap::new, Collectors.toList()));
        Map<Integer, ProductOptionItem> itemsById = items.stream()
            .collect(Collectors.toMap(ProductOptionItem::getId, Function.identity()));
        Map<Integer, Integer> groupOrderById = groups.stream()
            .collect(Collectors.toMap(ProductOptionGroup::getId, groups::indexOf));

        detail.setOptionGroups(groups.stream()
            .map(group -> ProductDetailResponse.OptionGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .items(itemsByGroupId.getOrDefault(group.getId(), Collections.emptyList()).stream()
                    .map(this::toDetailOptionItem)
                    .toList())
                .build())
            .toList());

        List<ProductVariant> variants = variantRepository.findByProdNoOrderByIdAsc(prodNo);
        List<Integer> variantIds = variants.stream()
            .map(ProductVariant::getId)
            .toList();
        List<ProductVariantItem> variantItems = variantIds.isEmpty()
            ? Collections.emptyList()
            : variantItemRepository.findByIdVariantIdIn(variantIds);
        Map<Integer, List<ProductVariantItem>> variantItemsByVariantId = variantItems.stream()
            .collect(Collectors.groupingBy(item -> item.getId().getVariantId()));

        detail.setVariants(variants.stream()
            .map(variant -> ProductDetailResponse.Variant.builder()
                .id(variant.getId())
                .stock(variant.getStock())
                .status(variant.getStatus())
                .items(variantItemsByVariantId.getOrDefault(variant.getId(), Collections.emptyList()).stream()
                    .map(item -> itemsById.get(item.getId().getOptionItemId()))
                    .filter(Objects::nonNull)
                    .sorted((left, right) -> Integer.compare(
                        groupOrderById.getOrDefault(left.getGroupId(), Integer.MAX_VALUE),
                        groupOrderById.getOrDefault(right.getGroupId(), Integer.MAX_VALUE)
                    ))
                    .map(this::toDetailOptionItem)
                    .toList())
                .build())
            .toList());
    }

    private ProductDetailResponse.OptionItem toDetailOptionItem(ProductOptionItem item) {
        return ProductDetailResponse.OptionItem.builder()
            .id(item.getId())
            .value(item.getValue())
            .build();
    }

    private void fillProductDetailNoticeValues(ProductDetailResponse detail, int prodNo) {
        ProductInfoNoticeTemplate template = ProductInfoNoticeTemplates.all().get(detail.getInfoNoticeType());
        if (template == null) {
            detail.setNoticeValues(Collections.emptyList());
            return;
        }

        detail.setInfoNoticeTypeName(template.name());

        Map<String, String> valuesByKey = noticeValueRepository.findByIdProdNo(prodNo).stream()
            .collect(Collectors.toMap(value -> value.getId().getNoticeKey(), ProductNoticeValue::getValue));
        List<ProductDetailResponse.NoticeValue> noticeValues = new ArrayList<>();

        for (ProductInfoNoticeField field : template.fields()) {
            noticeValues.add(ProductDetailResponse.NoticeValue.builder()
                .key(field.key())
                .label(field.label())
                .value(valuesByKey.getOrDefault(field.key(), ""))
                .build());
        }

        detail.setNoticeValues(noticeValues);
    }
}
