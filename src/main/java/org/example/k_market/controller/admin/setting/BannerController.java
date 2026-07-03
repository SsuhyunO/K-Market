package org.example.k_market.controller.admin.setting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BannerController {

    @GetMapping("/admin/banner-management")
    public String bannerManagement() {
        return "admin/setting/banner";
    }
}
