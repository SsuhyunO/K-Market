package org.example.k_market.service.order;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.OrderDAO;
import org.example.k_market.dto.coupon.CouponIssueDTO; // 실제 패키지/DTO명에 맞게 수정 필요
import org.example.k_market.dto.order.OrderCreateRequestDTO;
import org.example.k_market.dto.order.OrderDTO;
import org.example.k_market.dto.order.OrderItemDTO;
import org.example.k_market.dto.order.OrderItemRequestDTO;
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
import org.example.k_market.service.admin.CouponIssueService; // 이미 존재하는 서비스, findById/markAsUsed 메서드 추가 필요
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final CartRepository cartRepository;
    private final OrderDAO orderDAO;
    private final CouponIssueService couponIssueService;  // 기존 서비스, 메서드 확장 필요

    // ─────────────────────────────────────────────
    // 기존 코드 (조회/계산용) - 그대로 유지
    // ─────────────────────────────────────────────

    public List<OrderItemViewDTO> getOrderItemsDirect(Integer prodVariantId, Integer count) {
        ProductVariant variant = variantRepository.findById(prodVariantId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 옵션입니다."));

        validateOrderableVariant(variant);

        if (variant.getStock() < count) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        Product product = productRepository.findById(variant.getProdNo())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        validateOrderableProduct(product);

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
                .freeShipping(shippingFee <= 0)
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
        return "/files/" + thumb1FileId;
    }

    public List<OrderItemViewDTO> getOrderItemsFromCart(List<Integer> cartNoList, String memberUid) {
        if (memberUid == null || memberUid.isBlank() || cartNoList == null || cartNoList.isEmpty()) {
            return List.of();
        }

        List<Integer> targetCartNos = cartNoList.stream()
                .filter(cartNo -> cartNo != null && cartNo > 0)
                .distinct()
                .toList();

        if (targetCartNos.isEmpty()) {
            return List.of();
        }

        return cartRepository.findByMemberUidAndCartNoIn(memberUid, targetCartNos).stream()
                .map(cart -> getOrderItemsDirect(cart.getProdVariantId(), cart.getCount()).getFirst().toBuilder()
                        .cartNo(cart.getCartNo())
                        .build())
                .toList();
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

    // ─────────────────────────────────────────────
    // 신규: 주문 생성 (결제하기 버튼 클릭 시 호출)
    // Entity 없이 DTO만 사용, 저장은 MyBatis(OrderMapper)로 처리
    // ─────────────────────────────────────────────

    @Transactional
    public int createOrder(String memberUid, OrderCreateRequestDTO req) {

        // ===== 1. 상품별 실제 가격 재조회 (클라이언트 값 신뢰 X) =====
        List<OrderItemDTO> orderItems = new ArrayList<>();

        int orderPrice = 0;      // 상품 원가 합계
        int productDiscount = 0; // 상품자체할인 합계
        int shippingFee = 0;

        for (OrderItemRequestDTO itemReq : req.getItems()) {
            ProductVariant variant = variantRepository.findById(itemReq.getProdVariantId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "존재하지 않는 옵션입니다. prodVariantId=" + itemReq.getProdVariantId()));

            validateOrderableVariant(variant);

            if (variant.getStock() < itemReq.getCount()) {
                throw new IllegalStateException("재고가 부족합니다. prodVariantId=" + itemReq.getProdVariantId());
            }

            Product product = productRepository.findById(variant.getProdNo())
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
            validateOrderableProduct(product);

            int qty = itemReq.getCount();
            int price = product.getPrice();
            int discountRate = product.getDiscount();
            int discountedUnitPrice = price * (100 - discountRate) / 100;
            int lineTotal = discountedUnitPrice * qty; // 쿠폰 반영 전, 상품할인만 반영

            OrderItemDTO item = OrderItemDTO.builder()
                    .prodVariantId(variant.getId())
                    .count(qty)
                    .price(discountedUnitPrice)
                    .total(lineTotal)
                    .build();

            orderItems.add(item);

            orderPrice += price * qty;
            productDiscount += (price - discountedUnitPrice) * qty;
            shippingFee += product.getDeliveryFee(); // freeShipping 쿠폰 적용은 아래에서 반영
        }

        // ===== 2. 쿠폰 검증 및 반영 =====
        int couponDiscount = 0;
        boolean freeShipping = false;

        if (req.getCouponIssueId() != null) {
            int issueNo = req.getCouponIssueId().intValue();
            CouponIssueDTO couponIssue = couponIssueService.getCouponIssueByNo(issueNo);

            if (couponIssue == null) {
                throw new IllegalArgumentException("쿠폰을 찾을 수 없습니다.");
            }
            if (!memberUid.equals(couponIssue.getMemberUid())) {
                throw new IllegalStateException("본인의 쿠폰이 아닙니다.");
            }
            if (couponIssue.getStatus() != 0 /* STATUS_READY */) {
                throw new IllegalStateException("이미 사용되었거나 사용할 수 없는 쿠폰입니다.");
            }
            // expireDate 포맷이 "yyyy-MM-dd..." 형태라고 가정 (실제 포맷 확인 필요)
            if (couponIssue.getExpireDate() != null
                    && java.time.LocalDate.parse(couponIssue.getExpireDate().substring(0, 10))
                    .isBefore(java.time.LocalDate.now())) {
                throw new IllegalStateException("만료된 쿠폰입니다.");
            }

            String couponType = couponIssue.getCouponType(); // "PRODUCT" or "ORDER"
            String benefit = couponIssue.getBenefit();       // "1000", "10", "DELIVERY_FREE"

            if ("PRODUCT".equals(couponType)) {
                Integer targetVariantId = req.getTargetVariantId();
                if (targetVariantId == null) {
                    throw new IllegalArgumentException("쿠폰을 적용할 상품이 지정되지 않았습니다.");
                }

                OrderItemDTO targetItem = orderItems.stream()
                        .filter(i -> i.getProdVariantId() == targetVariantId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("쿠폰 적용 대상 상품이 주문에 없습니다."));

                int baseAmount = targetItem.getPrice() * targetItem.getCount();

                if ("DELIVERY_FREE".equals(benefit)) {
                    freeShipping = true;
                } else if (benefit.length() <= 2) {
                    int rate = Integer.parseInt(benefit);
                    couponDiscount = baseAmount * rate / 100;
                } else {
                    couponDiscount = Integer.parseInt(benefit);
                }

                if (couponDiscount > 0) {
                    targetItem.setTotal(targetItem.getTotal() - couponDiscount);
                    targetItem.setPrice(targetItem.getTotal() / targetItem.getCount());
                }

            } else if ("ORDER".equals(couponType)) {
                int priceAfterProductDiscount = orderPrice - productDiscount;

                if ("DELIVERY_FREE".equals(benefit)) {
                    freeShipping = true;
                } else if (benefit.length() <= 2) {
                    int rate = Integer.parseInt(benefit);
                    couponDiscount = priceAfterProductDiscount * rate / 100;
                } else {
                    couponDiscount = Integer.parseInt(benefit);
                }

                if (couponDiscount > 0) {
                    distributeDiscountProportionally(orderItems, couponDiscount);
                }
            } else {
                throw new IllegalStateException("알 수 없는 쿠폰 타입입니다: " + couponType);
            }

            if (freeShipping) {
                shippingFee = 0;
            }

            couponIssueService.markAsUsed(issueNo, memberUid);
        }

        // ===== 3. 포인트 검증 =====
        int usedPoints = req.getUsedPoints() != null ? req.getUsedPoints() : 0;
        int orderTotalBeforePoint = orderPrice - productDiscount - couponDiscount;

        if (usedPoints > 0) {
            if (usedPoints < 5000) {
                throw new IllegalArgumentException("포인트는 5,000원 이상부터 사용할 수 있습니다.");
            }
            if (usedPoints > orderTotalBeforePoint) {
                throw new IllegalArgumentException("전체주문금액을 초과하여 포인트를 사용할 수 없습니다.");
            }
            // TODO: 회원 보유 포인트 잔액 검증 (MemberService 등에서 조회)
        }

        // ===== 4. 최종 금액 계산 및 order 저장 =====
        int orderTotal = orderTotalBeforePoint - usedPoints + shippingFee;

        OrderDTO order = OrderDTO.builder()
                .memberUid(memberUid)
                .orderPrice(orderPrice)
                .orderDiscount(productDiscount + couponDiscount)
                .orderTotal(orderTotal)
                .usedPoints(usedPoints)
                .receiver(req.getReceiver())
                .phone(req.getPhone())
                .zipCode(req.getZipCode())
                .addr1(req.getAddr1())
                .addr2(req.getAddr2())
                .payMethod(req.getPayMethod())
                .status("PAID") // 실제 PG 연동 전이므로 임시로 바로 PAID 처리
                .orderNote(req.getOrderNote())
                .build();

        orderDAO.insertOrder(order); // order.orderNo가 채워짐 (useGeneratedKeys)

        for (OrderItemDTO item : orderItems) {
            item.setOrderNo(order.getOrderNo());
        }
        orderDAO.insertOrderItems(orderItems);

        // ===== 5. 재고 차감 =====
        for (OrderItemDTO item : orderItems) {
            int updated = variantRepository.decreaseStock(item.getProdVariantId(), item.getCount());
            if (updated == 0) {
                throw new IllegalStateException("재고가 부족합니다. prodVariantId=" + item.getProdVariantId());
            }
        }

        // TODO: 포인트 차감/적립 반영 (Point 관련 서비스 연동)

        return order.getOrderNo();
    }

    private void validateOrderableVariant(ProductVariant variant) {
        if (!"ON_SALE".equals(variant.getStatus())) {
            throw new IllegalStateException("판매중인 상품 옵션이 아닙니다. prodVariantId=" + variant.getId());
        }
    }

    private void validateOrderableProduct(Product product) {
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new IllegalStateException("판매중인 상품이 아닙니다. prodNo=" + product.getProdNo());
        }
    }

    /**
     * ORDER 타입 쿠폰 할인을 각 order_item에 상품금액 비율대로 배분한다.
     * 마지막 아이템에서 나머지(반올림 오차)를 보정하여 총합이 정확히 맞도록 한다.
     */
    private void distributeDiscountProportionally(List<OrderItemDTO> orderItems, int totalDiscount) {
        int totalBase = orderItems.stream()
                .mapToInt(i -> i.getPrice() * i.getCount())
                .sum();

        int distributed = 0;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItemDTO item = orderItems.get(i);
            int base = item.getPrice() * item.getCount();

            int share;
            if (i == orderItems.size() - 1) {
                share = totalDiscount - distributed;
            } else {
                share = base * totalDiscount / totalBase;
                distributed += share;
            }

            item.setTotal(item.getTotal() - share);
            item.setPrice(item.getTotal() / item.getCount());
        }
    }
}
