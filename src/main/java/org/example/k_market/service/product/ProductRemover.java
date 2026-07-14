package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.product.Product;
import org.example.k_market.entity.product.ProductOptionGroup;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.repository.cart.CartRepository;
import org.example.k_market.repository.order.OrderItemRepository;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.example.k_market.repository.product.ProductOptionGroupRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.example.k_market.service.admin.FileService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProductRemover {
    private static final String STOPPED_STATUS = "STOPPED";

    private final ProductRepository productRepository;
    private final ProductNoticeValueRepository noticeValueRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileService fileService;

    public ProductRemovalResult removeAll(List<Integer> prodNos) {
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

        List<Integer> targetVariantIds = findVariantIdsByProdNos(targetProdNos);
        if (!targetVariantIds.isEmpty()) {
            cartRepository.deleteByProdVariantIdIn(targetVariantIds);
        }

        Set<Integer> referencedProdNos = findOrderReferencedProdNos(targetProdNos);
        List<Integer> stoppedProdNos = targetProdNos.stream()
            .filter(referencedProdNos::contains)
            .toList();
        List<Integer> hardDeleteProdNos = targetProdNos.stream()
            .filter(prodNo -> !referencedProdNos.contains(prodNo))
            .toList();

        if (!stoppedProdNos.isEmpty()) {
            productRepository.updateStatusByProdNoIn(stoppedProdNos, STOPPED_STATUS);
            variantRepository.updateStatusByProdNoIn(stoppedProdNos, STOPPED_STATUS);
            markOptionsDeleted(stoppedProdNos);
        }

        int hardDeletedCount = hardDeleteProdNos.isEmpty()
            ? 0
            : removeByHardDelete(hardDeleteProdNos);

        return new ProductRemovalResult(
            hardDeletedCount,
            stoppedProdNos.size(),
            buildRemovalMessage(hardDeletedCount, stoppedProdNos.size())
        );
    }

    private String buildRemovalMessage(int hardDeletedCount, int stoppedCount) {
        if (stoppedCount == 0) {
            return "선택한 상품이 삭제되었습니다.";
        }

        if (hardDeletedCount == 0) {
            return "주문에서 참조 중인 상품은 판매중지 처리되었습니다.";
        }

        return "선택한 상품 중 "
            + hardDeletedCount
            + "건은 삭제, "
            + stoppedCount
            + "건은 주문 이력 보존을 위해 판매중지 처리되었습니다.";
    }

    private Set<Integer> findOrderReferencedProdNos(List<Integer> prodNos) {
        return new HashSet<>(orderItemRepository.findReferencedProdNos(prodNos));
    }

    private List<Integer> findVariantIdsByProdNos(List<Integer> prodNos) {
        return variantRepository.findByProdNoIn(prodNos).stream()
            .map(ProductVariant::getId)
            .toList();
    }

    private void markOptionsDeleted(List<Integer> prodNos) {
        List<Integer> groupIds = optionGroupRepository.findByProdNoIn(prodNos).stream()
            .map(ProductOptionGroup::getId)
            .toList();

        if (!groupIds.isEmpty()) {
            optionItemRepository.markDeletedByGroupIdIn(groupIds);
        }
        optionGroupRepository.markDeletedByProdNoIn(prodNos);
    }

    private int removeByHardDelete(List<Integer> prodNos) {
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

        if (!variantIds.isEmpty()) {
            cartRepository.deleteByProdVariantIdIn(variantIds);
        }
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

        return products.size();
    }
}
