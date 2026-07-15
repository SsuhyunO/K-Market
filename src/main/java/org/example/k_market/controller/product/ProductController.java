package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryContextResponse;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.dto.product.response.ProductListPageResponse;
import org.example.k_market.dto.product.response.ProductViewPageResponse;
import org.example.k_market.entity.Member;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.MemberService;
import org.example.k_market.service.PointService;
import org.example.k_market.service.admin.CouponIssueService;
import org.example.k_market.service.order.OrderService;
import org.example.k_market.service.product.ProductViewPageReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/product")
public class ProductController {

    private final MemberService memberService;
    private final PointService pointService;
    private final OrderService orderService;
    private final CouponIssueService couponIssueService;
    private final CategoryService categoryService;
    private final ProductViewPageReader productViewPageReader;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam("category") int cateId) {
        CategoryContextResponse categoryContext = categoryService.getContext(cateId);
        if (categoryContext.hasRedirect()) {
            return "redirect:/product/list?category=" + categoryContext.redirectCategoryId();
        }

        model.addAttribute("page", new ProductListPageResponse(
            categoryContext.parentCate(),
            categoryContext.subCate()
        ));
        return "product/list";
    }

    @GetMapping("/view")
    public String view(@RequestParam("prodNo") int prodNo, Model model) {
        ProductViewPageResponse page = productViewPageReader.getViewPage(prodNo);
        model.addAttribute("page", page);
        return "product/view";
    }
    
    @GetMapping("/order")
    public String order(@RequestParam(required = false) List<Integer> cartNoList,
                        @RequestParam(required = false) Integer prodVariantId,
                        @RequestParam(required = false) Integer count,
                        HttpSession session,
                        Model model) {
        String memberUid = (String) session.getAttribute("loginMember");
        Member member = memberService.findByUid(memberUid);
        Integer sessionMemberLevel = (Integer) session.getAttribute("loginMemberLevel");
        int loginMemberLevel = sessionMemberLevel != null
                ? sessionMemberLevel
                : (member.getMemberLevel() != null ? member.getMemberLevel() : 1);

        // 1. 주문 상품 리스트 조회
        List<OrderItemViewDTO> orderItems;
        if (cartNoList != null && !cartNoList.isEmpty()) {
            orderItems = orderService.getOrderItemsFromCart(cartNoList, memberUid);
        } else if (prodVariantId != null) {
            orderItems = orderService.getOrderItemsDirect(prodVariantId, count);
        } else {
            return "redirect:/cart";
        }

        if (orderItems.isEmpty()) {
            return "redirect:/cart";
        }

        // 2. 회원 정보로 배송지 기본값 채우기
        model.addAttribute("recvName", member.getName());
        model.addAttribute("recvPhone", member.getPhone());
        model.addAttribute("recvZip", member.getZipCode());
        model.addAttribute("recvAddr1", member.getAddr1());
        model.addAttribute("recvAddr2", member.getAddr2());
        model.addAttribute("availablePoint", pointService.getBalance(memberUid));

        // 3. 쿠폰
        model.addAttribute("availableCoupons", couponIssueService.getAvailableCoupons(member.getUid(), orderItems));

        // 4. 주문 상품 + 합계
        int productTotal = orderService.calcProductTotal(orderItems);
        int discountTotal = orderService.calcDiscountTotal(orderItems);
        int shippingTotal = orderService.calcShippingTotal(orderItems);
        int earnPoint = orderService.calcEarnPoint(orderItems) * loginMemberLevel;

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

    @GetMapping("/complete")
    public String complete(@RequestParam("orderNo") int orderNo, Model model, HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");

        // 서비스로부터 화면 출력을 위한 가공된 주문 데이터 가져오기
        // ⚠️ 완벽한 구현을 위해 아래 OrderService에 추가할 메서드를 호출합니다.
        Map<String, Object> completeData = orderService.getCompletePageData(orderNo, memberUid);

        // 맵에 담긴 모든 데이터를 Model에 그대로 세팅
        model.addAllAttributes(completeData);

        return "product/complete";
    }

    @GetMapping("/search")
    public String search() {
        return "product/search";
    }
}
