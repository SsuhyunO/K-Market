package org.example.k_market.controller.admin.coupon;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.dto.seller.SellerDto;
import org.example.k_market.entity.Seller;
import org.example.k_market.service.SellerService;
import org.example.k_market.service.admin.CouponService;
import org.example.k_market.util.PageInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController {
    private final CouponService couponService;
    private final SellerService sellerService;

    @GetMapping({"/list", "", "/"})
    public String list(@RequestParam(defaultValue = "1") int page,
                       HttpSession session,
                       Model model) {
        String loginUid = (String) session.getAttribute("loginMember");

        // 최고관리자는 loginUid가 null이므로, null일 때는 seller 조회를 건너뜀
        if (loginUid != null) {
            Seller seller = sellerService.findByUid(loginUid);
            model.addAttribute("companyName", seller.getCompanyName());
        } else {
            model.addAttribute("companyName", "최고관리자"); // 혹은 null, 원하는 기본값
        }

        int totalCount = couponService.getTotalCount();
        PageInfo pageInfo = new PageInfo(page, totalCount); // 기본값 pageSize=10, pageBlockSize=5

        List<CouponDTO> couponList = couponService.getCouponList(page, pageInfo.getPageSize());
        model.addAttribute("couponList", couponList);
        model.addAttribute("pageInfo", pageInfo);

        return "admin/coupon/list";
    }

    @PostMapping("/register")
    public String register(CouponDTO dto, HttpSession session){
        String loginUid = (String) session.getAttribute("loginMember");
        dto.setSellerUid(loginUid); // 폼값 무시하고 세션값으로 덮어쓰기

        couponService.register(dto);

        return "redirect:/admin/coupon";
    }

    @PatchMapping("/{couponNo}/end")
    @ResponseBody
    public ResponseEntity<Void> endCoupon(@PathVariable int couponNo) {
        couponService.endCoupon(couponNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/used")
    public String used() {
        return "admin/coupon/used";
    }
}
