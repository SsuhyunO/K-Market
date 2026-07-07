package org.example.k_market.controller.admin.setting;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.AdminConfigDTO;
import org.example.k_market.service.admin.AdminConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/site-settings")
public class SettingsController {

    private final AdminConfigService adminConfigService;

    // 기본설정 화면 조회
    @GetMapping
    public String siteSettings(Model model) {

        AdminConfigDTO config = adminConfigService.findById(1);

        model.addAttribute("config", config);

        return "admin/setting/settings";
    }

    // 사이트 정보 수정
    @PostMapping("/modify-site-settings")
    public String modifySiteSettings(AdminConfigDTO dto, RedirectAttributes redirectAttributes) {

        adminConfigService.modifySiteSettings(dto);

        redirectAttributes.addFlashAttribute("message", "사이트 정보가 수정되었습니다.");

        return "redirect:/admin/site-settings";
    }

    // 로고 정보 수정
    @PostMapping("/modify-site-logo")
    public String modifySiteLogo(@RequestParam("headerLogo") MultipartFile headerLogo,
                                 @RequestParam("footerLogo") MultipartFile footerLogo,
                                 @RequestParam("favicon") MultipartFile favicon,
                                 RedirectAttributes redirectAttributes) {

        adminConfigService.modifySiteLogo(headerLogo, footerLogo, favicon);

        redirectAttributes.addFlashAttribute("message", "로고 정보가 수정되었습니다.");

        return "redirect:/admin/site-settings";
    }

    // 기업 정보 수정
    @PostMapping("/modify-corporate-info")
    public String modifyCorporateInfo(AdminConfigDTO dto, RedirectAttributes redirectAttributes) {

        adminConfigService.modifyCorporateInfo(dto);

        redirectAttributes.addFlashAttribute("message", "기업 정보가 수정되었습니다.");

        return "redirect:/admin/site-settings";
    }

    // 고객센터 정보 수정
    @PostMapping("/modify-customer-support-info")
    public String modifyCustomerSupportInfo(AdminConfigDTO dto, RedirectAttributes redirectAttributes) {

        adminConfigService.modifyCustomerSupportInfo(dto);

        redirectAttributes.addFlashAttribute("message", "고객센터 정보가 수정되었습니다.");

        return "redirect:/admin/site-settings";
    }

    // 카피라이트 수정
    @PostMapping("/modify-copyright")
    public String modifyCopyright(AdminConfigDTO dto, RedirectAttributes redirectAttributes) {

        adminConfigService.modifyCopyright(dto);

        redirectAttributes.addFlashAttribute("message", "카피라이트 정보가 수정되었습니다.");

        return "redirect:/admin/site-settings";
    }
}