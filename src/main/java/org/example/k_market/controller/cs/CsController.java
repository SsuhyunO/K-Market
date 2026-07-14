package org.example.k_market.controller.cs;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.FaqDTO;
import org.example.k_market.dto.admin.NoticeDTO;
import org.example.k_market.dto.admin.QnaDTO;
import org.example.k_market.service.admin.FaqService;
import org.example.k_market.service.admin.NoticeService;
import org.example.k_market.service.admin.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cs")
@RequiredArgsConstructor
public class CsController {

    private final NoticeService noticeService;
    private final FaqService faqService;
    private final QnaService qnaService;


    /* =========================================================
       고객센터 메인
       ========================================================= */

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {

        List<NoticeDTO> noticeList =
                noticeService.getNoticeList(1, null);

        List<FaqDTO> faqList =
                faqService.getFaqList(1, null);

        List<QnaDTO> qnaList =
                qnaService.getQnaList(1, null, null);

        model.addAttribute(
                "noticeList",
                noticeList.stream()
                        .limit(5)
                        .toList()
        );

        model.addAttribute(
                "faqList",
                faqList.stream()
                        .limit(6)
                        .toList()
        );

        model.addAttribute(
                "qnaList",
                qnaList.stream()
                        .limit(5)
                        .toList()
        );

        return "cs/index";
    }


    /* =========================================================
       공지사항
       ========================================================= */

    @GetMapping("/noticeList")
    public String noticeList(
            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,
            @RequestParam(
                    name = "type",
                    required = false
            ) String type,
            Model model
    ) {
        int safePage = Math.max(page, 1);

        String selectedType =
                type == null
                        ? ""
                        : type.trim();

        model.addAttribute(
                "noticeList",
                noticeService.getNoticeList(
                        safePage,
                        selectedType
                )
        );

        model.addAttribute(
                "pageInfo",
                noticeService.getPageInfo(
                        safePage,
                        selectedType
                )
        );

        model.addAttribute(
                "selectedType",
                selectedType
        );

        return "cs/noticeList";
    }

    @GetMapping("/noticeView")
    public String noticeView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        model.addAttribute(
                "notice",
                noticeService.getNotice(boardNo)
        );

        return "cs/noticeView";
    }


/* =========================================================
   자주하는 질문
   ========================================================= */

    @GetMapping("/faqList")
    public String faqList(
            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,

            @RequestParam(
                    name = "category1",
                    required = false
            ) String category1,

            @RequestParam(
                    name = "category2",
                    required = false
            ) String category2,

            Model model
    ) {
        int safePage = Math.max(page, 1);

        String selectedCategory1 =
                category1 == null
                        ? ""
                        : category1.trim();

        String selectedCategory2 =
                category2 == null
                        ? ""
                        : category2.trim();

        // 1차 유형이 없으면 2차 유형만 단독 검색하지 않도록 초기화
        if (selectedCategory1.isBlank()) {
            selectedCategory2 = "";
        }

        model.addAttribute(
                "faqList",
                faqService.getFaqList(
                        safePage,
                        selectedCategory1,
                        selectedCategory2
                )
        );

        model.addAttribute(
                "pageInfo",
                faqService.getPageInfo(
                        safePage,
                        selectedCategory1,
                        selectedCategory2
                )
        );

        model.addAttribute(
                "selectedCategory1",
                selectedCategory1
        );

        model.addAttribute(
                "selectedCategory2",
                selectedCategory2
        );

        return "cs/faqList";
    }

    @GetMapping("/faqView")
    public String faqView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        model.addAttribute(
                "faq",
                faqService.getFaq(boardNo)
        );

        return "cs/faqView";
    }

    /* =========================================================
       문의하기
       ========================================================= */

    @GetMapping("/qnaList")
    public String qnaList(
            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,
            @RequestParam(
                    name = "category1",
                    required = false
            ) String category1,
            @RequestParam(
                    name = "category2",
                    required = false
            ) String category2,
            Model model
    ) {
        int safePage = Math.max(page, 1);

        String selectedCategory1 =
                category1 == null
                        ? ""
                        : category1.trim();

        String selectedCategory2 =
                category2 == null
                        ? ""
                        : category2.trim();

        if (selectedCategory1.isBlank()) {
            selectedCategory2 = "";
        }

        model.addAttribute(
                "qnaList",
                qnaService.getQnaList(
                        safePage,
                        selectedCategory1,
                        selectedCategory2
                )
        );

        model.addAttribute(
                "pageInfo",
                qnaService.getPageInfo(
                        safePage,
                        selectedCategory1,
                        selectedCategory2
                )
        );

        model.addAttribute(
                "selectedCategory1",
                selectedCategory1
        );

        model.addAttribute(
                "selectedCategory2",
                selectedCategory2
        );

        return "cs/qnaList";
    }

    @GetMapping("/qnaView")
    public String qnaView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        model.addAttribute(
                "qna",
                qnaService.getQna(boardNo)
        );

        return "cs/qnaView";
    }

    @GetMapping("/qnaWrite")
    public String qnaWrite(
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "로그인 후 문의글을 작성할 수 있습니다."
            );

            return "redirect:/member/login";
        }

        model.addAttribute(
                "qna",
                new QnaDTO()
        );

        return "cs/qnaWrite";
    }

    @PostMapping("/qnaWrite")
    public String qnaWrite(
            QnaDTO dto,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "로그인 후 문의글을 작성할 수 있습니다."
            );

            return "redirect:/member/login";
        }

        dto.setMemberUid(principal.getName());

        qnaService.insertQna(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "문의글이 등록되었습니다."
        );

        return "redirect:/cs/qnaList";
    }
}