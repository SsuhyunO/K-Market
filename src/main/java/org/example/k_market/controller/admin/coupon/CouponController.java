package org.example.k_market.controller.admin.coupon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController {
    @GetMapping({"/list", "", "/"})
    public String list() {
        return "admin/coupon/list";
    }

    @PostMapping("/register")
    public String register(){
        return "redirect:/admin/coupon";
    }

    @GetMapping("/used")
    public String used() {
        return "admin/coupon/used";
    }
}
