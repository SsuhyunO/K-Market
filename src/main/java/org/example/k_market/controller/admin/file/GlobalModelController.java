package org.example.k_market.controller.admin.file;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.k_market.service.admin.AdminConfigService;
import org.example.k_market.service.admin.BannerService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
@Log4j2
public class GlobalModelController {

    private final AdminConfigService adminConfigService;
    private final BannerService bannerService;

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        if (request.getDispatcherType() == DispatcherType.ERROR) return;

        String path = getRequestPath(request);
        if (path.startsWith("/api/") || path.startsWith("/files/")) {
            return;
        }

        if (path.equals("/admin") || path.startsWith("/admin/")) {
            return;
        }

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

        if ("/".equals(path) || "/index".equals(path)) {
            // 메인 슬라이더 배너
            try {
                model.addAttribute(
                        "mainSliderBanners",
                        bannerService.findEnabledBannersByType("mainSlider")
                );
            } catch (Exception e) {
                model.addAttribute("mainSliderBanners", null);
            }
        }
    }

    private String getRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }

        return requestUri;
    }
}
