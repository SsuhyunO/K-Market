package org.example.k_market.controller.admin.setting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsController {

    @GetMapping("/admin/terms-management")
    public String termsManagement() {
        return "admin/setting/terms";
    }
}
