package org.example.k_market.controller.admin.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @GetMapping("/register")
    public String register() {
        return "admin/product/register";
    }

    @GetMapping("/list")
    public String list() {
        return "admin/product/list";
    }

    @GetMapping("/edit")
    public String edit() {
        return "admin/product/edit";
    }
}
