package org.example.k_market.service.order;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.entity.admin.File;
import org.example.k_market.entity.product.Product;
import org.example.k_market.entity.product.ProductOptionItem;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.repository.admin.FileRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final ProductOptionItemRepository optionItemRepository;
    private final FileRepository fileRepository;

    public List<OrderItemViewDTO> getOrderItemsDirect(Integer prodVariantId, Integer count) {
        ProductVariant variant = variantRepository.findById(prodVariantId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 옵션입니다."));

        if (variant.getStock() < count) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        Product product = productRepository.findById(variant.getProdNo())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        String optionText = buildOptionText(variant.getId());
        String thumbnailUrl = buildThumbnailUrl(product.getThumb1FileId());

        int price = product.getPrice();
        int discountRate = product.getDiscount();
        int point = product.getPoint();
        int shippingFee = product.getDeliveryFee();
        int lineTotal = price * count * (100 - discountRate) / 100;

        return List.of(OrderItemViewDTO.builder()
                .prodVariantId(variant.getId())
                .thumbnailUrl(thumbnailUrl)
                .productName(product.getProdName())
                .sellerUid(product.getSellerUid())
                .optionText(optionText)
                .quantity(count)
                .price(price)
                .discountRate(discountRate)
                .point(point)
                .freeShipping(false) // 쿠폰 적용 전 기본값
                .shippingFee(shippingFee)
                .lineTotal(lineTotal)
                .build());
    }

    private String buildOptionText(int variantId) {
        List<ProductVariantItem> variantItems = variantItemRepository.findByIdVariantIdIn(List.of(variantId));
        List<Integer> optionItemIds = variantItems.stream()
                .map(vi -> vi.getId().getOptionItemId())
                .toList();

        List<ProductOptionItem> optionItems = optionItemRepository.findAllById(optionItemIds);
        return optionItems.stream()
                .map(ProductOptionItem::getValue)
                .collect(Collectors.joining(" / "));
    }

    private String buildThumbnailUrl(Integer thumb1FileId) {
        if (thumb1FileId == null || thumb1FileId <= 0) {
            return null;
        }
        return fileRepository.findById(thumb1FileId)
                .map(this::toFileUrl)
                .orElse(null);
    }

    private String toFileUrl(File file) {
        // TODO: 실제 파일 서빙 경로 규칙에 맞게 수정
        // 예시 1) 정적 리소스로 저장: /uploads/{id}.{extension}
        return "/uploads/" + file.getId() + "." + file.getExtension();
        // 예시 2) 파일 다운로드 컨트롤러 경유: /files/{id}
        // return "/files/" + file.getId();
    }

    public List<OrderItemViewDTO> getOrderItemsFromCart(List<Integer> cartNoList) {
        // TODO: cart 쪽 완성 후 구현
        return List.of();
    }

    public int calcProductTotal(List<OrderItemViewDTO> items) {
        return items.stream().mapToInt(i -> i.getPrice() * i.getQuantity()).sum();
    }

    public int calcDiscountTotal(List<OrderItemViewDTO> items) {
        return items.stream()
                .mapToInt(i -> (i.getPrice() * i.getDiscountRate() / 100) * i.getQuantity())
                .sum();
    }

    public int calcShippingTotal(List<OrderItemViewDTO> items) {
        return items.stream()
                .filter(i -> !i.isFreeShipping())
                .mapToInt(OrderItemViewDTO::getShippingFee)
                .sum();
    }

    public int calcEarnPoint(List<OrderItemViewDTO> items) {
        return items.stream().mapToInt(i -> i.getPoint() * i.getQuantity()).sum();
    }
}