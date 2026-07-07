package org.example.k_market.controller.admin.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminProductController")
@RequestMapping("/admin/product")
@RequiredArgsConstructor
@Slf4j
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
    public String register(ProductRegisterRequest request, HttpSession session, Model model) {
        String sellerUid = (String) session.getAttribute("loginMember");
        if (sellerUid == null) {
            addProductFormModel(model);
            model.addAttribute("productErrorMessage", "로그인이 필요합니다.");
            return "redirect:admin/product/register?register=failed";
        }

        try {
            productService.register(request, sellerUid);
            return "redirect:/admin/product/list?register=success";
        } catch (RuntimeException exception) {
            addProductFormModel(model);
            model.addAttribute("productErrorMessage", getRegisterErrorMessage(exception));
            return "redirect:admin/product/register?register=failed";
        }
    }

    private void addProductFormModel(Model model) {
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
    }

    private String getRegisterErrorMessage(RuntimeException exception) {
        if (exception instanceof IllegalArgumentException || exception instanceof IllegalStateException) {
            return exception.getMessage();
        }

        log.error("상품 등록 실패", exception);
        return "상품 등록 중 문제가 발생했습니다. 입력값을 확인한 뒤 다시 시도해주세요.";
    }
}
