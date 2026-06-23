package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BannerManagementController {

    @GetMapping("/admin/banner-management")
    public String bannerManagement(@RequestParam(defaultValue ="mainTop") String bannerCategory, Model model) {

        String tableHeader = switch(bannerCategory) {
            case "mainSlider" -> "메인 슬라이더 배너";
            case "productDetailView" -> "상품 상세보기 배너";
            case "userLogin" -> "회원로그인 배너";
            case "마이페이지 배너" -> "마이페이지 배너";
            default -> "메인 상단배너";
        };

        model.addAttribute("bannerCategory", bannerCategory);
        model.addAttribute("tableHeader", tableHeader);

        return "admin/banner-management";
    }
}
