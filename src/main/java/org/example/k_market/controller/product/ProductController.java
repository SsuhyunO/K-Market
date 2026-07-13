package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryDTO;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.example.k_market.entity.Member;
import org.example.k_market.enums.product.ProductSortType;
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

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final MemberService memberService;
    private final OrderService orderService;
    private final CouponIssueService couponIssueService;
    private final CategoryService categoryService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/product/list")
    public String list(Model model, @RequestParam("category") int cateId) {
        // aside의 베스트 상품을 보여주기 위해 필요 (빈 리스트면 placeholder, 실제 데이터면 그 데이터)
        CategoryDTO subCate = categoryService.getCategory(cateId);
        CategoryDTO parentCate = categoryService.getCategory(subCate.getParentId());
        model.addAttribute("parentCate", parentCate);
        model.addAttribute("subCate", subCate);
        model.addAttribute("bestProducts", java.util.List.of());

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