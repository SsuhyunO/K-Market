package org.example.k_market.controller.admin.file;

import lombok.RequiredArgsConstructor;
import org.example.k_market.service.admin.AdminConfigService;
import org.example.k_market.service.admin.BannerService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalModelController {

    private final AdminConfigService adminConfigService;
    private final BannerService bannerService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {

        // 사이트 기본설정 정보
        try {
            model.addAttribute("siteConfig", adminConfigService.findById(1));
        } catch (Exception e) {
            model.addAttribute("siteConfig", null);
        }

        // 메인 상단 배너
        try {
            model.addAttribute(
                    "mainTopBanners",
                    bannerService.findEnabledBannersByType("mainTop")
            );
        } catch (Exception e) {
            model.addAttribute("mainTopBanners", null);
        }

        // 메인 슬라이더 배너
        try {
            model.addAttribute(
                    "mainSliderBanners",
                    bannerService.findEnabledBannersByType("mainSlider")
            );
        } catch (Exception e) {
            model.addAttribute("mainSliderBanners", null);
        }

        // 상품 상세보기 배너
        try {
            model.addAttribute(
                    "productDetailViewBanners",
                    bannerService.findEnabledBannersByType("productDetailView")
            );
        } catch (Exception e) {
            model.addAttribute("productDetailViewBanners", null);
        }

        // 회원 로그인 배너
        try {
            model.addAttribute(
                    "userLoginBanners",
                    bannerService.findEnabledBannersByType("userLogin")
            );
        } catch (Exception e) {
            model.addAttribute("userLoginBanners", null);
        }

        // 마이페이지 배너
        try {
            model.addAttribute(
                    "myPageBanners",
                    bannerService.findEnabledBannersByType("myPage")
            );
        } catch (Exception e) {
            model.addAttribute("myPageBanners", null);
        }
    }
}