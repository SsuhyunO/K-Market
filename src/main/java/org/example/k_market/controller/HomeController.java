package org.example.k_market.controller;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.enums.product.MainProductSortType;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("hitProducts", productService.getMainProducts(MainProductSortType.HIT));
        model.addAttribute("recommendProducts", productService.getMainProducts(MainProductSortType.RATING));
        model.addAttribute("newProducts", productService.getMainProducts(MainProductSortType.CREATED_AT));
        model.addAttribute("discountProducts", productService.getMainProducts(MainProductSortType.DISCOUNT));
        return "index";
    }
}
