package org.example.k_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompanyController {
    @GetMapping("/company/index")
    public String index(){
        return "/company/index";
    }

    @GetMapping("/company/culture")
    public String culture() {
        return "company/culture";
    }

    /**
     * 소식과 이야기 — 카테고리 필터링 지원
     * 예: /company/story?category=interview
     */
    @GetMapping("/company/story")
    public String story(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("category", category);
        // model.addAttribute("storyList", storyService.findByCategory(category));
        return "company/story";
    }

    /**
     * 채용 — 채용 목록만 출력 (지원하기 기능 없음, 와이어프레임 명시사항)
     */
    @GetMapping("/company/recruit")
    public String recruit(Model model) {
        // model.addAttribute("recruitList", recruitService.findAll());
        return "company/recruit";
    }

    /**
     * 미디어 — 유튜브 영상 목록
     */
    @GetMapping("/company/media")
    public String media(Model model) {
        // model.addAttribute("mediaList", mediaService.findAll());
        return "company/media";
    }
}
