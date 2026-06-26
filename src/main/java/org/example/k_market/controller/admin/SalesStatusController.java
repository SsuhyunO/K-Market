package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class SalesStatusController {

    @GetMapping("/sales-status")
    public String salesStatus() {
        return "/admin/sales-status";
    }
}
