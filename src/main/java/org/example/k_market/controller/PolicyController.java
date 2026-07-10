package org.example.k_market.controller;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PolicyArticleDTO;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.PolicyService;
import org.example.k_market.util.PolicyParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 약관 페이지 5종 매핑
 *
 * templates/policy/buyer.html    ↔ GET /policy/buyer
 * templates/policy/seller.html   ↔ GET /policy/seller
 * templates/policy/finance.html  ↔ GET /policy/finance
 * templates/policy/location.html ↔ GET /policy/location
 * templates/policy/privacy.html  ↔ GET /policy/privacy
 *
 * 반환 문자열은 templates 폴더 기준 상대경로 + 확장자 제외.
 * 예: "policy/buyer" → templates/policy/buyer.html 을 렌더링
 */
@RequiredArgsConstructor
@Controller
public class PolicyController {
    private final PolicyService policyService;
    private final CategoryService categoryService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/policy/buyer")
    public String buyer(Model model) {
        addArticles(model, "BUYER");
        return "policy/buyer";
    }

    @GetMapping("/policy/seller")
    public String seller(Model model) {
        addArticles(model, "SELLER");
        return "policy/seller";
    }

    @GetMapping("/policy/finance")
    public String finance(Model model) {
        addArticles(model, "FINANCE");
        return "policy/finance";
    }

    @GetMapping("/policy/location")
    public String location(Model model) {
        addArticles(model, "LOCATION");
        return "policy/location";
    }

    @GetMapping("/policy/privacy")
    public String privacy(Model model) {
        addArticles(model, "PRIVACY");
        return "policy/privacy";
    }

    private void addArticles(Model model, String policyType) {
        String rawContent = policyService.getPolicyContent(policyType);
        List<PolicyArticleDTO> articles = PolicyParser.splitArticles(rawContent);
        model.addAttribute("articles", articles);
    }
}
