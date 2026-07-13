package org.example.k_market.controller.admin.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductInfoNoticeTemplates;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.ProductService;
import org.example.k_market.service.product.ProductRemovalResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller("adminProductController")
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ManagementProductController {

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
    public String edit(@RequestParam("prodNo") int prodNo, Model model) {
        addProductFormModel(model);
        model.addAttribute("product", productService.getProductDetailForManagement(prodNo));
        return "admin/product/edit";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam("prodNo") int prodNo, ProductRegisterRequest request, RedirectAttributes redirectAttributes) {
        productService.modify(prodNo, request);
        redirectAttributes.addFlashAttribute("productMessage", "상품이 수정되었습니다.");
        return "redirect:/admin/product/list?edit=success";
    }

    @PostMapping("/register")
    public String register(ProductRegisterRequest request, HttpSession session) {
        String sellerUid = (String) session.getAttribute("loginMember");

        productService.register(request, sellerUid);
        return "redirect:/admin/product/list?register=success";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam(value = "productNo", required = false) List<Integer> productNos,
                         RedirectAttributes redirectAttributes) {
        ProductRemovalResult result = productService.remove(productNos);
        redirectAttributes.addFlashAttribute("productMessage", result.message());
        return "redirect:/admin/product/list?remove=success";
    }

    private void addProductFormModel(Model model) {
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        model.addAttribute("productInfoNoticeTemplates", ProductInfoNoticeTemplates.all());
    }
}
