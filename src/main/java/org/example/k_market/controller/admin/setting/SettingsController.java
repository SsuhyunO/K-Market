package org.example.k_market.controller.admin.setting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/site-settings")
public class SettingsController {

    @GetMapping
    public String siteSettings() {
        return "admin/setting/settings";
    }

    @PostMapping("/modify-site-settings")
    public String modifySiteSettings() {
        return "redirect:/admin/site-settings";
    }

    @PostMapping("/modify-site-logo")
    public String modifySiteLogo() {
        return "redirect:/admin/site-settings";
    }

    @PostMapping("/modify-corporate-info")
    public String modifyCorporateInfo() {
        return "redirect:/admin/site-settings";
    }

    @PostMapping("/modify-customer-support-info")
    public String modifyCustomerSupportInfo() {
        return "redirect:/admin/site-settings";
    }
}
