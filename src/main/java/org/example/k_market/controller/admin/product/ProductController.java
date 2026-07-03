package org.example.k_market.controller.admin.product;

import org.example.k_market.dto.product.ProductInfoNoticeTemplates;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
        return "admin/product/register";
    }

    @GetMapping("/list")
    public String list() {
        return "admin/product/list";
    }

    @GetMapping("/edit")
    public String edit(Model model) {
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
        return "admin/product/edit";
    }
}
