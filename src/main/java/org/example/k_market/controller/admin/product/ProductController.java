package org.example.k_market.controller.admin.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("adminProductController")
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/register")
    public String register(Model model) {
        addProductFormModel(model);
        return "admin/product/register";
    }

    @GetMapping("/list")
    public String list() {
        return "admin/product/list";
    }

    @GetMapping("/edit")
    public String edit(Model model) {
        addProductFormModel(model);
        return "admin/product/edit";
    }

    @PostMapping("/register")
    public String register(ProductRegisterRequest request, HttpSession session) {
        String sellerUid = (String) session.getAttribute("loginMember");

        productService.register(request, sellerUid);
        return "redirect:/admin/product/list?register=success";
    }

    private void addProductFormModel(Model model) {
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
    }
}
