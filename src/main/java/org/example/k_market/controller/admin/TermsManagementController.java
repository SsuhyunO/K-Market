package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsManagementController {

    @GetMapping("/admin/terms-management")
    public String termsManagement() {
        return "admin/terms-management";
    }
}
