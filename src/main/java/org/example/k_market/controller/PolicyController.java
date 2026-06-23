package org.example.k_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
@Controller
public class PolicyController {

    @GetMapping("/policy/buyer")
    public String buyer() {
        return "policy/buyer";
    }

    @GetMapping("/policy/seller")
    public String seller() {
        return "policy/seller";
    }

    @GetMapping("/policy/finance")
    public String finance() {
        return "policy/finance";
    }

    @GetMapping("/policy/location")
    public String location() {
        return "policy/location";
    }

    @GetMapping("/policy/privacy")
    public String privacy() {
        return "policy/privacy";
    }
}
