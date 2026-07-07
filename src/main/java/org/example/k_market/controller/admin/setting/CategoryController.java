package org.example.k_market.controller.admin.setting;

import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dto.category.request.CategorySaveRequest;
import org.example.k_market.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping("/category-management")
    public String category(Model model) {
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
        return "admin/setting/category";
    }

    @PostMapping("/category-management")
    @ResponseBody
    public ResponseEntity saveCategories(@RequestBody CategorySaveRequest request) {
        categoryService.saveCategories(request);
        return ResponseEntity.ok().build();
    }
}
