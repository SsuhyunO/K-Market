package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryManagementController {

    @GetMapping("/admin/category-management")
    public String category() {
        return "/admin/category-management";
    }
}
