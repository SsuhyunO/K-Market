package org.example.k_market.controller.admin.cs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminCsController")
@RequestMapping("/admin/cs")
public class CsController {

    @GetMapping({"/notice/list", "", "/", "/notice"})
    public String noticeList() {
        return "admin/cs/notice/list";
    }

    @GetMapping("/notice/view")
    public String noticeView() {
        return "admin/cs/notice/view";
    }

    @GetMapping("/notice/write")
    public String noticeWrite() {
        return "admin/cs/notice/write";
    }

    @GetMapping("/notice/modify")
    public String noticeModify() {
        return "admin/cs/notice/modify";
    }

    @GetMapping({"/faq/list", "/faq"})
    public String faqList() {
        return "admin/cs/faq/list";
    }

    @GetMapping("/faq/view")
    public String faqView() {
        return "admin/cs/faq/view";
    }

    @GetMapping("/faq/write")
    public String faqWrite() {
        return "admin/cs/faq/write";
    }

    @GetMapping("/faq/modify")
    public String faqModify() {
        return "admin/cs/faq/modify";
    }

    @GetMapping({"/qna/list", "/qna"})
    public String qnaList() {
        return "admin/cs/qna/list";
    }

    @GetMapping("/qna/view")
    public String qnaView() {
        return "admin/cs/qna/view";
    }

    @GetMapping("/qna/reply")
    public String qnaReply() {
        return "admin/cs/qna/reply";
    }

    @GetMapping("/recruit/list")
    public String recruitList() {
        return "admin/cs/recruit/list";
    }
}
