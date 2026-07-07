package org.example.k_market.controller.admin.coupon;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.dto.seller.SellerDto;
import org.example.k_market.entity.Seller;
import org.example.k_market.service.SellerService;
import org.example.k_market.service.admin.CouponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController {
    private final CouponService couponService;
    private final SellerService sellerService;

    @GetMapping({"/list", "", "/"})
    public String list(HttpSession session, Model model) {
        String loginUid = (String) session.getAttribute("loginMember");
        Seller seller = sellerService.findByUid(loginUid);
        model.addAttribute("companyName", seller.getCompanyName());

        List<CouponDTO> couponList = couponService.findAll();
        model.addAttribute("couponList", couponList);

        return "admin/coupon/list";
    }

    @PostMapping("/register")
    public String register(CouponDTO dto, HttpSession session){
        String loginUid = (String) session.getAttribute("loginMember");
        dto.setSellerUid(loginUid); // 폼값 무시하고 세션값으로 덮어쓰기

        couponService.register(dto);

        return "redirect:/admin/coupon";
    }

    @GetMapping("/used")
    public String used() {
        return "admin/coupon/used";
    }
}
