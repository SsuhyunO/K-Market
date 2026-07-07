package org.example.k_market.controller.admin.setting;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.service.admin.BannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BannerController {

    private final BannerService bannerService;

    @GetMapping({"/admin/banner-management", "/admin/banner-management/"})
    public String bannerManagement(@RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                                   Model model) {

        List<BannerDTO> dtoList = bannerService.findByBannerType(bannerCategory);

        System.out.println("===== 배너관리 조회 =====");
        System.out.println("bannerCategory = " + bannerCategory);
        System.out.println("dtoList.size = " + dtoList.size());

        for (BannerDTO dto : dtoList) {
            System.out.println("bannerId = " + dto.getBannerId()
                    + ", bannerType = " + dto.getBannerType()
                    + ", name = " + dto.getName());
        }

        model.addAttribute("dtoList", dtoList);
        model.addAttribute("bannerCategory", bannerCategory);
        model.addAttribute("bannerTitle", getBannerTitle(bannerCategory));

        return "admin/setting/banner";
    }

    @PostMapping("/admin/banner/register")
    public String register(BannerDTO dto,
                           @RequestParam("bannerFile") MultipartFile bannerFile,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime,
                           @RequestParam(required = false) String backgroundColor,
                           @RequestParam(required = false) String position,
                           @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                           RedirectAttributes redirectAttributes) {

        dto.setBgColor(backgroundColor);

        if (position != null && !position.isBlank()) {
            dto.setBannerType(position);
        } else {
            dto.setBannerType(bannerCategory);
        }

        dto.setStartAt(toLocalDateTime(startDate, startTime));
        dto.setEndAt(toLocalDateTime(endDate, endTime));
        dto.setEnabled(true);

        bannerService.register(dto, bannerFile);

        redirectAttributes.addFlashAttribute("message", "배너가 등록되었습니다.");

        return "redirect:/admin/banner-management?bannerCategory=" + dto.getBannerType();
    }

    @GetMapping("/admin/banner/toggle")
    public String toggle(@RequestParam Integer bannerId,
                         @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                         RedirectAttributes redirectAttributes) {

        bannerService.toggleEnabled(bannerId);

        redirectAttributes.addFlashAttribute("message", "배너 상태가 변경되었습니다.");

        return "redirect:/admin/banner-management?bannerCategory=" + bannerCategory;
    }

    @PostMapping("/admin/banner/delete")
    public String deleteSelected(@RequestParam(required = false) List<Integer> bannerNo,
                                 @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                                 RedirectAttributes redirectAttributes) {

        bannerService.deleteSelected(bannerNo);

        redirectAttributes.addFlashAttribute("message", "선택한 배너가 삭제되었습니다.");

        return "redirect:/admin/banner-management?bannerCategory=" + bannerCategory;
    }

    private LocalDateTime toLocalDateTime(String date, String time) {

        if (date == null || date.isBlank()) {
            return null;
        }

        LocalDate localDate = LocalDate.parse(date);

        LocalTime localTime;

        if (time == null || time.isBlank()) {
            localTime = LocalTime.of(0, 0);
        } else {
            localTime = LocalTime.parse(time);
        }

        return LocalDateTime.of(localDate, localTime);
    }

    private String getBannerTitle(String bannerCategory) {

        return switch (bannerCategory) {
            case "mainTop" -> "메인상단 배너";
            case "mainSlider" -> "메인슬라이더 배너";
            case "productDetailView" -> "상품 상세보기 배너";
            case "userLogin" -> "회원로그인 배너";
            case "myPage" -> "마이페이지 배너";
            default -> "메인상단 배너";
        };
    }
}