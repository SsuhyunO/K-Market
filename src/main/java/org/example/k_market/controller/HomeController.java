package org.example.k_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        // 실제 데이터 연결 전까지는 빈 리스트만 넣어도 충분
        // (aside.html이 빈 리스트일 때 placeholder 3개를 자동으로 보여줌)
        model.addAttribute("bestProducts", List.of());

        // model.addAttribute("hitProducts", productService.findHit());
        // model.addAttribute("recommendProducts", productService.findRecommend());
        // model.addAttribute("newProducts", productService.findNew());
        // model.addAttribute("popularProducts", productService.findPopular());
        // model.addAttribute("discountProducts", productService.findDiscount());

        return "index";
    }
}