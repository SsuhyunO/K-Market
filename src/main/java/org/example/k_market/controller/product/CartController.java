package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.product.response.ProductCartPageResponse;
import org.example.k_market.service.cart.CartService;
import org.example.k_market.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final CategoryService categoryService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping
    public String cart(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");
        ProductCartPageResponse page = cartService.getCartPage(memberUid);
        model.addAttribute("page", page);

        return "product/cart";
    }
}
