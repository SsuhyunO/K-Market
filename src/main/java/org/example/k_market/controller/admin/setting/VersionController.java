package org.example.k_market.controller.admin.setting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VersionController {

    @GetMapping("/admin/version-management")
    public String versionManagement() {
        return "admin/setting/version";
    }
}
