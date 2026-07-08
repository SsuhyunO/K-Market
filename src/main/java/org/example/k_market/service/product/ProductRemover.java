package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.product.Product;
import org.example.k_market.entity.product.ProductOptionGroup;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.example.k_market.repository.product.ProductOptionGroupRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.example.k_market.service.admin.FileService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductRemover {
    private final ProductRepository productRepository;
    private final ProductNoticeValueRepository noticeValueRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final FileService fileService;

    public void removeAll(List<Integer> prodNos) {
        if (prodNos == null || prodNos.isEmpty()) {
            throw new IllegalArgumentException("삭제할 상품을 선택해주세요.");
        }

        List<Integer> targetProdNos = prodNos.stream()
            .filter(prodNo -> prodNo != null && prodNo > 0)
            .distinct()
            .toList();

        if (targetProdNos.isEmpty()) {
            throw new IllegalArgumentException("삭제할 상품을 선택해주세요.");
        }

        removeByHardDelete(targetProdNos);
    }

    private void removeByHardDelete(List<Integer> prodNos) {
        List<Product> products = productRepository.findAllById(prodNos);
        List<Integer> fileIds = products.stream()
            .flatMap(product -> Stream.of(
                product.getThumb1FileId(),
                product.getThumb2FileId(),
                product.getThumb3FileId(),
                product.getDetailInfoFileId()
            ))
            .filter(fileId -> fileId != null && fileId > 0)
            .distinct()
            .toList();

        List<Integer> groupIds = optionGroupRepository.findByProdNoIn(prodNos).stream()
            .map(ProductOptionGroup::getId)
            .toList();
        List<Integer> variantIds = variantRepository.findByProdNoIn(prodNos).stream()
            .map(ProductVariant::getId)
            .toList();

        noticeValueRepository.deleteAllByProdNoIn(prodNos);
        if (!variantIds.isEmpty()) {
            variantItemRepository.deleteAllByVariantIdIn(variantIds);
        }
        variantRepository.deleteAllByProdNoIn(prodNos);
        if (!groupIds.isEmpty()) {
            optionItemRepository.deleteAllByGroupIdIn(groupIds);
        }
        optionGroupRepository.deleteAllByProdNoIn(prodNos);
        productRepository.deleteAllByIdInBatch(prodNos);

        fileIds.stream()
            .filter(Objects::nonNull)
            .forEach(fileService::delete);
    }
}
