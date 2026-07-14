package org.example.k_market.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.k_market.service.admin.PointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    private final PointService pointService;

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
                .prodNo(variant.getProdNo())
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

        // 💡 추가: 같은 prodNo가 이미 배송비를 부과받았는지 체크
        Set<Integer> shippingChargedProdNos = new HashSet<>();

        return cartRepository.findByMemberUidAndCartNoIn(memberUid, targetCartNos).stream()
                .map(cart -> {
                    OrderItemViewDTO baseItem = getOrderItemsDirect(cart.getProdVariantId(), cart.getCount()).getFirst();

                    // 💡 추가: 이미 이 prodNo로 배송비가 부과됐으면 0원/무료로 덮어쓰기
                    boolean alreadyCharged = !shippingChargedProdNos.add(baseItem.getProdNo());
                    if (alreadyCharged) {
                        baseItem = baseItem.toBuilder()
                                .shippingFee(0)
                                .freeShipping(true)
                                .build();
                    }

                    return baseItem.toBuilder()
                            .cartNo(cart.getCartNo())
                            .build();
                })
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

        int orderPrice = 0;
        int productDiscount = 0;
        int totalEarnPoint = 0;

        // 💡 변경: 같은 prodNo가 이미 배송비를 부과받았는지 체크
        Set<Integer> shippingChargedProdNos = new HashSet<>();
        int shippingFee = 0; // 💡 order 전체 합계는 여전히 필요 (포인트 계산 등에 사용)

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
            int point = product.getPoint();
            int discountRate = product.getDiscount();
            int discountedUnitPrice = price * (100 - discountRate) / 100;
            int lineTotal = discountedUnitPrice * qty;

            // 💡 추가: 같은 prodNo면 첫 아이템에만 배송비 부과, 나머지는 0
            int itemShippingFee = 0;
            if (shippingChargedProdNos.add(variant.getProdNo())) {
                // add()가 true를 반환 = 이 prodNo가 처음 등장함
                itemShippingFee = product.getDeliveryFee();
                shippingFee += itemShippingFee;
            }

            OrderItemDTO item = OrderItemDTO.builder()
                    .prodNo(variant.getProdNo())
                    .prodVariantId(variant.getId())
                    .count(qty)
                    .price(discountedUnitPrice)
                    .total(lineTotal)
                    .shippingFee(itemShippingFee)   // 💡 추가
                    .build();

            orderItems.add(item);

            orderPrice += price * qty;
            productDiscount += (price - discountedUnitPrice) * qty;
            totalEarnPoint += point * qty;
        }

        // ===== 2. 쿠폰 검증 및 반영 =====
        int couponDiscount = 0;
        boolean freeShipping = false;

        int issueNo = 0;
        if (req.getCouponIssueId() != null) {
            issueNo = req.getCouponIssueId().intValue();
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

            String couponType = couponIssue.getCouponType(); // "PRODUCT", "ORDER", "DELIVERY"
            String benefit = couponIssue.getBenefit();       // "1000", "10" (DELIVERY는 무시됨)

            if ("PRODUCT".equals(couponType)) {
                Integer targetVariantId = req.getTargetVariantId();
                if (targetVariantId == null) {
                    throw new IllegalArgumentException("쿠폰을 적용할 상품이 지정되지 않았습니다.");
                }

                OrderItemDTO selectedItem = orderItems.stream()
                        .filter(i -> i.getProdVariantId() == targetVariantId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("쿠폰 적용 대상 상품이 주문에 없습니다."));

                int targetProdNo = selectedItem.getProdNo();

                List<OrderItemDTO> targetItems = orderItems.stream()
                        .filter(i -> i.getProdNo() == targetProdNo)
                        .toList();

                int baseAmount = targetItems.stream()
                        .mapToInt(i -> i.getPrice() * i.getCount())
                        .sum();

                if (benefit.length() <= 2) {
                    int rate = Integer.parseInt(benefit);
                    couponDiscount = baseAmount * rate / 100;
                } else {
                    couponDiscount = Integer.parseInt(benefit);
                }

                if (couponDiscount > 0) {
                    distributeDiscountProportionally(targetItems, couponDiscount);
                }

            } else if ("ORDER".equals(couponType)) {
                int priceAfterProductDiscount = orderPrice - productDiscount;

                if (benefit.length() <= 2) {
                    int rate = Integer.parseInt(benefit);
                    couponDiscount = priceAfterProductDiscount * rate / 100;
                } else {
                    couponDiscount = Integer.parseInt(benefit);
                }

                if (couponDiscount > 0) {
                    distributeDiscountProportionally(orderItems, couponDiscount);
                }

            } else if ("DELIVERY".equals(couponType)) {
                // 💡 DELIVERY 타입: 할인 없이 배송비 전체 무료
                freeShipping = true;

            } else {
                throw new IllegalStateException("알 수 없는 쿠폰 타입입니다: " + couponType);
            }

            if (freeShipping) {
                shippingFee = 0;
                orderItems.forEach(item -> item.setShippingFee(0));  // 💡 추가
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

            int pointBalance = pointService.getBalance(memberUid);
            if (usedPoints > pointBalance) {
                throw new IllegalArgumentException("보유 포인트가 부족합니다.");
            }
        }

        // ===== 4. 최종 금액 계산 및 order 저장 =====
        int orderTotal = orderTotalBeforePoint - usedPoints + shippingFee;

        OrderDTO order = OrderDTO.builder()
                .memberUid(memberUid)
                .orderPrice(orderPrice)
                .orderDiscount(productDiscount + couponDiscount)
                .couponIssueId(req.getCouponIssueId())
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

        // ===== 6. 포인트 사용/적립 반영 =====
        if (usedPoints > 0) {
            pointService.usePoint(memberUid, usedPoints, order.getOrderNo());
        }
        if (totalEarnPoint > 0) {
            pointService.earnPoint(memberUid, totalEarnPoint, order.getOrderNo());
        }

        // =================================================================
        // 💡 [추가] 7. 결제 및 주문 완료에 따른 장바구니 목록 삭제 (주문한 상품만 제거)
        // =================================================================
        try {
            // 이번 주문에 포함된 prodVariantId 목록 추출
            List<Integer> orderedVariantIds = req.getItems().stream()
                    .map(OrderItemRequestDTO::getProdVariantId)
                    .toList();

            if (!orderedVariantIds.isEmpty()) {
                // 해당 회원의 장바구니 품목 중, 방금 주문한 상품(prodVariantId)만 매칭하여 삭제
                // (이미 CartRepository에 memberUid와 prodVariantId 목록으로 지우는 쿼리 메서드가 있다면 그것을 사용하고,
                //  없다면 안전하게 아래와 같이 루프를 돌며 삭제를 수행합니다.)
                for (Integer variantId : orderedVariantIds) {
                    cartRepository.findByMemberUidAndProdVariantId(memberUid, variantId)
                            .ifPresent(cartRepository::delete);
                }
                log.info("주문 성공으로 인한 장바구니 품목 삭제 완료. 회원: {}, 삭제된 상품 수: {}", memberUid, orderedVariantIds.size());
            }
        } catch (Exception e) {
            // 장바구니 삭제 실패로 인해 전체 주문이 롤백되는 것을 방지하고 싶다면 로그만 남깁니다.
            // 만약 반드시 같이 지워져야만 한다면 try-catch를 빼고 바로 실행하여 예외를 던지게 두면 됩니다.
            log.error("주문 완료 후 장바구니 삭제 중 오류 발생: ", e);
        }

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

    // OrderService.java 내부에 추가
    public Map<String, Object> getCompletePageData(int orderNo, String memberUid) {
        // 1. 주문 기본 정보 조회 (MyBatis 혹은 기존 래퍼가 있다고 가정)
        // 만약 orderDAO에 단건 조회 메서드가 없다면 생성하셔야 합니다. 예: orderDAO.selectOrder(orderNo)
        OrderDTO order = orderDAO.selectOrderByNo(orderNo);
        if (order == null) {
            throw new NoSuchElementException("해당 주문이 존재하지 않습니다.");
        }
        if (!order.getMemberUid().equals(memberUid)) {
            throw new IllegalStateException("권한이 없는 접근입니다.");
        }

        // 2. 주문에 속한 상품 리스트 조회 (orderDAO.selectOrderItemsByOrderNo)
        List<OrderItemDTO> savedItems = orderDAO.selectOrderItemsByOrderNo(orderNo);

        // 💡 추가: order_item에서 직접 배송비 합산
        int shippingTotal = savedItems.stream()
                .mapToInt(OrderItemDTO::getShippingFee)
                .sum();

        // 3. HTML(complete.html)에서 th:each="item : ${orderItems}" 구조에 맞게 View DTO로 가공
        List<OrderItemViewDTO> orderItems = savedItems.stream().map(item -> {
            // 기존 서비스 내 정의된 옵션 텍스트 및 썸네일 규칙 재활용
            ProductVariant variant = variantRepository.findById(item.getProdVariantId()).orElse(null);
            Product product = variant != null ? productRepository.findById(variant.getProdNo()).orElse(null) : null;

            String optionText = variant != null ? buildOptionText(variant.getId()) : "";
            String thumbnailUrl = product != null ? buildThumbnailUrl(product.getThumb1FileId()) : null;
            String productName = product != null ? product.getProdName() : "삭제된 상품";

            // 💡 총 할인액 역산 규칙: (원래 단가 * 수량) - 실제 저장된 총 아이템 합계 금액
            int originalTotal = (product != null ? product.getPrice() : item.getPrice()) * item.getCount();
            int discountAmount = originalTotal - item.getTotal();

            // complete.html에서 사용 중인 변수명에 맞게 매핑
            return OrderItemViewDTO.builder()
                    .prodVariantId(item.getProdVariantId())
                    .thumbnailUrl(thumbnailUrl)
                    .productName(productName)
                    .optionText(optionText)
                    .quantity(item.getCount())
                    .price(product != null ? product.getPrice() : item.getPrice())
                    .lineTotal(item.getTotal())
                    .discountAmount(Math.max(0, discountAmount))
                    .shippingFee(item.getShippingFee())          // 💡 추가 (선택)
                    .freeShipping(item.getShippingFee() == 0)    // 💡 추가 (선택)
                    .build();
        }).collect(Collectors.toList());

        // 4. 결제 수단 한글 치환 매핑
        String payMethodName = switch (order.getPayMethod()) {
            case "CARD" -> "신용카드";
            case "CHECK_CARD" -> "체크카드";
            case "BANK" -> "계좌이체";
            case "VBANK" -> "무통장입금";
            case "PHONE" -> "휴대폰결제";
            case "KAKAO" -> "카카오페이";
            default -> order.getPayMethod();
        };

        // 5. 총액 정보들 역산 및 세팅 (화면에 뿌려줄 값 정리)
        int productTotal = order.getOrderPrice(); // 원가 총합
        int finalPrice = order.getOrderTotal();   // 최종 결제액 (원가 - 할인 - 포인트 + 배송비)
        int usedPoints = order.getUsedPoints();

        // 전체 할인액 = 상품 자체 할인 + 쿠폰 할인
        int discountTotal = order.getOrderDiscount();

        // 배송 주소 문자열 조합
        String recvFullAddr = "[" + order.getZipCode() + "] " + order.getAddr1() + " " + order.getAddr2();

        // 결과를 담아 Controller로 반환
        Map<String, Object> result = new HashMap<>();
        result.put("customerName", memberUid); // 필요 시 별도의 닉네임/이름 조회 로직 대체 가능
        result.put("orderItems", orderItems);
        result.put("productTotal", productTotal);
        result.put("discountTotal", discountTotal);
        result.put("usedPoints", usedPoints);
        result.put("finalPrice", finalPrice);
        result.put("orderNo", order.getOrderNo());
        result.put("payMethodName", payMethodName);
        result.put("createdAt", order.getCreatedAt());
        result.put("ordererName", memberUid);
        result.put("ordererPhone", order.getPhone());
        result.put("recvName", order.getReceiver());
        result.put("recvPhone", order.getPhone());
        result.put("recvFullAddr", recvFullAddr);
        result.put("shippingTotal", shippingTotal);

        return result;
    }

    // =================================================================
    // 관리자 페이지용: 주문 목록 조회 및 페이징 처리를 위한 메서드
    // =================================================================

    /**
     * 조건에 맞는 전체 주문 건수 조회
     */
    public int getTotalCount(String searchType, String keyword) {
        // 검색 조건이 비어있으면 null로 처리하여 DAO로 넘김 (동적 쿼리 처리를 위해)
        if (searchType != null && searchType.isBlank()) searchType = null;
        if (keyword != null && keyword.isBlank()) keyword = null;

        return orderDAO.selectOrderCount(searchType, keyword);
    }

    /**
     * 조건에 맞는 페이징된 주문 목록 조회
     */
    public List<OrderDTO> getOrderList(String searchType, String keyword, int page, int pageSize) {
        if (searchType != null && searchType.isBlank()) searchType = null;
        if (keyword != null && keyword.isBlank()) keyword = null;

        // DB에서 가져올 시작 위치(offset) 계산
        int offset = (page - 1) * pageSize;

        return orderDAO.selectOrders(searchType, keyword, offset, pageSize);
    }
}
