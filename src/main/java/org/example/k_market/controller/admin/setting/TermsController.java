package org.example.k_market.controller.admin.setting;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PolicyDTO;
import org.example.k_market.service.PolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class TermsController {
    private final PolicyService policyService;

    @GetMapping("/admin/terms-management")
    public String termsManagement(Model model) {
        model.addAttribute("buyerPolicy", policyService.getPolicyContent("BUYER"));
        model.addAttribute("sellerPolicy", policyService.getPolicyContent("SELLER"));
        model.addAttribute("financePolicy", policyService.getPolicyContent("FINANCE"));
        model.addAttribute("locationPolicy", policyService.getPolicyContent("LOCATION"));
        model.addAttribute("privacyPolicy", policyService.getPolicyContent("PRIVACY"));

        return "admin/setting/terms";
    }

    @PostMapping("/admin/terms-management")
    public String updatePolicy(PolicyDTO policyDTO,
                               RedirectAttributes redirectAttributes) {

        policyService.updatePolicy(policyDTO);
        redirectAttributes.addFlashAttribute("message", "약관이 수정되었습니다.");

        return "redirect:/admin/terms-management";
    }
}
