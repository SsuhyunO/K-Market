package org.example.k_market.controller.admin.setting;

import org.example.k_market.dto.product.ProductInfoNoticeTemplates;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryController {

    @GetMapping("/admin/category-management")
    public String category(Model model) {
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
        return "admin/setting/category";
    }
}
