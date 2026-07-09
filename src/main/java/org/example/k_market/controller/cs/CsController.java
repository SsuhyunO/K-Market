package org.example.k_market.controller.cs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CsController {
    @GetMapping("/cs/index")
    public String index(){
        return "/cs/index";
    }

    @GetMapping("/cs/noticeList")
    public String noticeList() {
        return "cs/noticeList";
    }

    @GetMapping("/cs/noticeView")
    public String noticeView() {
        return "cs/noticeView";
    }

    @GetMapping("/cs/faqList")
    public String faqList() {
        return "cs/faqList";
    }

    @GetMapping("/cs/faqView")
    public String faqView() {
        return "cs/faqView";
    }

    @GetMapping("/cs/qnaList")
    public String qnaList() {
        return "cs/qnaList";
    }

    @GetMapping("/cs/qnaView")
    public String qnaView() {
        return "cs/qnaView";
    }

    @GetMapping("/cs/qnaWrite")
    public String qnaWrite() {
        return "cs/qnaWrite";
    }
}