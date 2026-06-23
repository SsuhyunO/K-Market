package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VersionManagementController {

    @GetMapping("/admin/version-management")
    public String versionManagement() {
        return "admin/version-management";
    }
}
