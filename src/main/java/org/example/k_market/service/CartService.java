package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.cart.CartAddRequest;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.entity.cart.Cart;
import org.example.k_market.entity.product.Product;
import org.example.k_market.entity.product.ProductOptionItem;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.repository.cart.CartRepository;
import org.example.k_market.repository.product.ProductOptionItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantItemRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
    private final ProductOptionItemRepository optionItemRepository;

    @Transactional
    public void add(String memberUid, CartAddRequest request) {
        if (memberUid == null || memberUid.isBlank()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        if (request.getProdVariantId() == null || request.getProdVariantId() <= 0) {
            throw new IllegalArgumentException("상품 옵션을 선택해주세요.");
        }

        int count = request.getCount() == null ? 1 : request.getCount();
        if (count <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        ProductVariant variant = variantRepository.findById(request.getProdVariantId())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품 옵션입니다."));
        validateCartableVariant(variant, count);

        Product product = productRepository.findById(variant.getProdNo())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new IllegalStateException("판매중인 상품이 아닙니다.");
        }
        int salePrice = calcSalePrice(product);

        cartRepository.findByMemberUidAndProdVariantId(memberUid, variant.getId())
            .ifPresentOrElse(
                cart -> {
                    int nextCount = cart.getCount() + count;
                    if (variant.getStock() < nextCount) {
                        throw new IllegalStateException("재고가 부족합니다.");
                    }
                    cart.setCount(nextCount);
                    cart.setPrice(salePrice);
                    cart.setTotal(salePrice * nextCount);
                },
                () -> cartRepository.save(Cart.builder()
                    .memberUid(memberUid)
                    .prodVariantId(variant.getId())
                    .count(count)
                    .price(salePrice)
                    .total(salePrice * count)
                    .build())
            );
    }

    public List<OrderItemViewDTO> getCartItems(String memberUid) {
        return cartRepository.findByMemberUidOrderByCartNoDesc(memberUid).stream()
            .map(this::toOrderItemView)
            .toList();
    }

    @Transactional
    public int remove(String memberUid, List<Integer> cartNos) {
        if (cartNos == null || cartNos.isEmpty()) {
            return 0;
        }

        List<Integer> targetCartNos = cartNos.stream()
            .filter(cartNo -> cartNo != null && cartNo > 0)
            .distinct()
            .toList();

        if (targetCartNos.isEmpty()) {
            return 0;
        }

        return cartRepository.deleteByCartNoInAndMemberUid(targetCartNos, memberUid);
    }

    private OrderItemViewDTO toOrderItemView(Cart cart) {
        ProductVariant variant = variantRepository.findById(cart.getProdVariantId())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품 옵션입니다."));
        Product product = productRepository.findById(variant.getProdNo())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        int lineTotal = calcSalePrice(product) * cart.getCount();

        return OrderItemViewDTO.builder()
            .cartNo(cart.getCartNo())
            .prodVariantId(variant.getId())
            .thumbnailUrl(product.getThumb1FileId() != null ? "/files/" + product.getThumb1FileId() : null)
            .productName(product.getProdName())
            .sellerUid(product.getSellerUid())
            .optionText(buildOptionText(variant.getId()))
            .quantity(cart.getCount())
            .price(product.getPrice())
            .discountRate(product.getDiscount())
            .point(product.getPoint())
            .freeShipping(product.getDeliveryFee() <= 0)
            .shippingFee(product.getDeliveryFee())
            .lineTotal(lineTotal)
            .build();
    }

    private String buildOptionText(int variantId) {
        List<ProductVariantItem> variantItems = variantItemRepository.findByIdVariantIdIn(List.of(variantId));
        List<Integer> optionItemIds = variantItems.stream()
            .map(vi -> vi.getId().getOptionItemId())
            .toList();

        if (optionItemIds.isEmpty()) {
            return "기본상품";
        }

        List<ProductOptionItem> optionItems = optionItemRepository.findAllById(optionItemIds);
        return optionItems.stream()
            .map(ProductOptionItem::getValue)
            .collect(Collectors.joining(" / "));
    }

    private void validateCartableVariant(ProductVariant variant, int count) {
        if (!"ON_SALE".equals(variant.getStatus())) {
            throw new IllegalStateException("판매중인 상품 옵션이 아닙니다.");
        }
        if (variant.getStock() < count) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }

    private int calcSalePrice(Product product) {
        return product.getPrice() * (100 - product.getDiscount()) / 100;
    }
}
