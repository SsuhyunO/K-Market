package org.example.k_market.controller.admin.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class SalesController {

    @GetMapping("/sales-status")
    public String salesStatus() {
        return "admin/shop/sales";
    }
}
