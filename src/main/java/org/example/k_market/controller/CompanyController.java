package org.example.k_market.controller;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.RecruitDTO;
import org.example.k_market.service.admin.RecruitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CompanyController {

    private final RecruitService recruitService;

    @GetMapping("/company/index")
    public String index() {
        return "company/index";
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

        return "company/story";
    }

    /**
     * 채용 — 관리자에서 등록한 채용공고 목록 출력
     */
    @GetMapping("/company/recruit")
    public String recruit(Model model) {

        List<RecruitDTO> recruitList = recruitService.findAll();

        model.addAttribute("recruitList", recruitList);

        return "company/recruit";
    }

    /**
     * 미디어 — 유튜브 영상 목록
     */
    @GetMapping("/company/media")
    public String media(Model model) {

        return "company/media";
    }
}