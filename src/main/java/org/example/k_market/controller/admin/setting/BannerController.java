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
                           @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                           RedirectAttributes redirectAttributes) {

        dto.setBgColor(backgroundColor);
        dto.setBannerType(bannerCategory);
        dto.setStartAt(toLocalDateTime(startDate, startTime));
        dto.setEndAt(toLocalDateTime(endDate, endTime));
        dto.setEnabled(true);

        System.out.println("===== 배너 등록 =====");
        System.out.println("bannerCategory = " + bannerCategory);
        System.out.println("dto.bannerType = " + dto.getBannerType());
        System.out.println("name = " + dto.getName());

        bannerService.register(dto, bannerFile);

        redirectAttributes.addFlashAttribute("message", "배너가 등록되었습니다.");

        return "redirect:/admin/banner-management?bannerCategory=" + bannerCategory;
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

    // 배너 수정 화면 이동
    @GetMapping("/admin/banner/modify")
    public String modifyForm(@RequestParam(required = false) Integer bannerId,
                             @RequestParam(required = false) Integer bannerNo,
                             @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Integer selectedBannerId = bannerId != null ? bannerId : bannerNo;

        if (selectedBannerId == null) {
            redirectAttributes.addFlashAttribute("message", "수정할 배너를 선택해주세요.");
            return "redirect:/admin/banner-management?bannerCategory=" + bannerCategory;
        }

        BannerDTO bannerDTO = bannerService.findById(selectedBannerId);

        model.addAttribute("banner", bannerDTO);
        model.addAttribute("bannerCategory", bannerCategory);
        model.addAttribute("bannerTitle", getBannerTitle(bannerCategory));

        return "admin/setting/banner-modify";
    }

    // 배너 수정 처리
    @PostMapping("/admin/banner/modify")
    public String modify(BannerDTO dto,
                         @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(required = false) String startTime,
                         @RequestParam(required = false) String endTime,
                         @RequestParam(required = false) String backgroundColor,
                         @RequestParam(required = false, defaultValue = "mainTop") String bannerCategory,
                         RedirectAttributes redirectAttributes) {

        dto.setBannerType(bannerCategory);
        dto.setBgColor(backgroundColor);
        dto.setStartAt(toLocalDateTime(startDate, startTime));
        dto.setEndAt(toLocalDateTime(endDate, endTime));

        System.out.println("===== 배너 수정 =====");
        System.out.println("bannerId = " + dto.getBannerId());
        System.out.println("bannerCategory = " + bannerCategory);
        System.out.println("dto.bannerType = " + dto.getBannerType());
        System.out.println("name = " + dto.getName());

        bannerService.modify(dto, bannerFile);

        redirectAttributes.addFlashAttribute("message", "배너가 수정되었습니다.");

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