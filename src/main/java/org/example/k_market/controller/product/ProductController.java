package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.entity.Member;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.MemberService;
import org.example.k_market.service.order.OrderService;
import org.example.k_market.service.admin.CouponIssueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 상품목록 페이지 매핑
 *
 * templates/product/list.html ↔ GET /product/list
 *
 * 쿼리 파라미터:
 *  - category : 1차 또는 2차 카테고리 ID (필수)
 *  - sort     : 정렬 기준 (기본값 sales)
 *               sales(판매많은순) | priceAsc(낮은가격순) | priceDesc(높은가격순)
 *               ratingDesc(평점높은순) | reviewDesc(후기많은순) | latest(최근등록순)
 *  - page     : 페이지 번호 (기본값 1), 10개씩 페이징
 */
@RequiredArgsConstructor
@Controller
public class ProductController {

    private static final int PAGE_SIZE = 10;
    private final MemberService memberService;
    private final OrderService orderService;
    private final CouponIssueService couponIssueService;
    private final CategoryService categoryService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/product/list")
    public String list(
            @RequestParam(required = false) Long category,
            @RequestParam(defaultValue = "sales") String sort,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("categoryId", category);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);

        // aside의 베스트 상품을 보여주기 위해 필요 (빈 리스트면 placeholder, 실제 데이터면 그 데이터)
        model.addAttribute("bestProducts", java.util.List.of());

        // ── 카테고리명 / 브레드크럼 정보 (실제로는 CategoryService에서 조회) ──
        // model.addAttribute("categoryName", categoryService.findName(category));
        // model.addAttribute("parentCategoryId", categoryService.findParentId(category));
        // model.addAttribute("parentCategoryName", categoryService.findParentName(category));

        // ── 정렬 기준에 따라 상품 목록 조회 (실제로는 ProductService에서 처리) ──
        // Sort sortOption = switch (sort) {
        //     case "priceAsc"    -> Sort.by("price").ascending();
        //     case "priceDesc"   -> Sort.by("price").descending();
        //     case "ratingDesc"  -> Sort.by("rating").descending();
        //     case "reviewDesc"  -> Sort.by("reviewCount").descending();
        //     case "latest"      -> Sort.by("createdAt").descending();
        //     default            -> Sort.by("salesCount").descending(); // sales
        // };
        // Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sortOption);
        // Page<Product> result = productService.findByCategory(category, pageable);
        // model.addAttribute("productList", result.getContent());
        // model.addAttribute("totalPages", result.getTotalPages());

        return "product/list";
    }

    @GetMapping("/product/view")
    public String view() {

        // 💡 나중에는 여기서 파라미터(id 등)를 받아서 DB에서 상품 정보를 조회한 뒤
        // model.addAttribute("product", productInfo); 처럼 넘겨주는 로직이 추가됩니다.
        // 하지만 지금은 화면만 띄우면 되므로 아주 심플합니다!

        return "product/view"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }

    @GetMapping("/product/cart")
    public String cart() {

        return "product/cart";
    }

    // TODO: 테스트용 default, 실제 배포 전 제거
    @GetMapping("/product/order")
    public String order(@RequestParam(required = false) List<Integer> cartNoList,
                        @RequestParam(required = false, defaultValue = "109") Integer prodVariantId,
                        @RequestParam(required = false, defaultValue = "2") Integer count,
                        HttpSession session,
                        Model model) {
        String memberUid = (String) session.getAttribute("loginMember");
        Member member = memberService.findByUid(memberUid);

        // 1. 주문 상품 리스트 조회
        List<OrderItemViewDTO> orderItems;
        if (cartNoList != null && !cartNoList.isEmpty()) {
            orderItems = orderService.getOrderItemsFromCart(cartNoList);
        } else if (prodVariantId != null) {
            orderItems = orderService.getOrderItemsDirect(prodVariantId, count);
        } else {
            return "redirect:/product/cart";
        }

        // 2. 회원 정보로 배송지 기본값 채우기
        model.addAttribute("recvName", member.getName());
        model.addAttribute("recvPhone", member.getPhone());
        model.addAttribute("recvZip", member.getZipCode());
        model.addAttribute("recvAddr1", member.getAddr1());
        model.addAttribute("recvAddr2", member.getAddr2());
        model.addAttribute("availablePoint", member.getPointBalance());

        // 3. 쿠폰
        model.addAttribute("availableCoupons", couponIssueService.getAvailableCoupons(member.getUid(), orderItems));

        // 4. 주문 상품 + 합계
        int productTotal = orderService.calcProductTotal(orderItems);
        int discountTotal = orderService.calcDiscountTotal(orderItems);
        int shippingTotal = orderService.calcShippingTotal(orderItems);
        int earnPoint = orderService.calcEarnPoint(orderItems);

        model.addAttribute("orderItems", orderItems);
        model.addAttribute("productTotal", productTotal);
        model.addAttribute("discountTotal", discountTotal);
        model.addAttribute("shippingTotal", shippingTotal);
        model.addAttribute("earnPoint", earnPoint);

        // usedPoint=0, coupon 미적용 상태의 초기값
        int usedPoint = 0;
        int orderTotal = productTotal - discountTotal; // 포인트/쿠폰 적용 전 금액
        int finalPrice = orderTotal + shippingTotal - usedPoint;

        model.addAttribute("usedPoint", usedPoint);
        model.addAttribute("orderTotal", orderTotal);
        model.addAttribute("finalPrice", finalPrice);

        return "product/order";
    }

    @GetMapping("/product/complete")
    public String complete() {

        return "product/complete";
    }

    @GetMapping("/product/search")
    public String search() {

        return "product/search";
    }
}