package org.example.k_market.controller.my;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.service.admin.BannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@Controller
public class MyPageController {

    private final BannerService bannerService;

    @ModelAttribute
    public void addMyPageBanner(Model model) {

        BannerDTO myPageBanner =
                bannerService.findFirstEnabledBannerByType("myPage");

        model.addAttribute("myPageBanner", myPageBanner);
    }

    @GetMapping("/my/home")
    public String home() {
        return "my/home";
    }

    @GetMapping("/my/order")
    public String order() {
        return "my/order";
    }

    @GetMapping("/my/point")
    public String point() {
        return "my/point";
    }

    @GetMapping("/my/coupon")
    public String coupon() {
        return "my/coupon";
    }

    @GetMapping("/my/review")
    public String review() {
        return "my/review";
    }

    @GetMapping("/my/qna")
    public String qna() {
        return "my/qna";
    }

    @GetMapping("/my/info")
    public String info() {
        return "my/info";
    }
}