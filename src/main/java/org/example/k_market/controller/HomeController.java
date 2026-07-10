package org.example.k_market.controller;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 메인 페이지(/) 매핑
 *
 * bestProducts를 모델에 담아주면 aside.html의
 * th:if="${bestProducts != null}" 조건이 true가 되어
 * 베스트 상품 영역이 자동으로 보입니다.
 *
 * 빈 리스트(List.of())를 넣으면 placeholder 3개가 보이고,
 * 실제 데이터를 넣으면 그 데이터가 보입니다.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/")
    public String index(Model model) {

        return "index";
    }
}