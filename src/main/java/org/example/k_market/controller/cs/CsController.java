package org.example.k_market.controller.cs;

import jakarta.servlet.http.HttpSession;
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

import java.util.List;

@Controller
@RequestMapping("/cs")
@RequiredArgsConstructor
public class CsController {

    private final NoticeService noticeService;
    private final FaqService faqService;
    private final QnaService qnaService;

    /* 고객센터 메인 */
    @GetMapping({"", "/", "/index"})
    public String index(Model model) {

        List<NoticeDTO> noticeList =
                noticeService.getNoticeList(
                        1,
                        null
                );

        List<FaqDTO> faqList =
                faqService.getFaqList(
                        1,
                        null
                );

        List<QnaDTO> qnaList =
                qnaService.getQnaList(
                        1,
                        null,
                        null
                );

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

    /* 공지사항 목록 */
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
        int safePage =
                Math.max(page, 1);

        String selectedType =
                normalize(type);

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

    /* 공지사항 상세 */
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

    /* 자주 묻는 질문 목록 */
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
        int safePage =
                Math.max(page, 1);

        String selectedCategory1 =
                normalize(category1);

        String selectedCategory2 =
                normalize(category2);

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

    /* 자주 묻는 질문 상세 */
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

    /* 문의글 목록 */
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
        int safePage =
                Math.max(page, 1);

        String selectedCategory1 =
                normalize(category1);

        String selectedCategory2 =
                normalize(category2);

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

    /* 문의글 상세 */
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

    /* 문의글 작성 화면 */
    @GetMapping("/qnaWrite")
    public String qnaWrite(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        String memberUid =
                (String) session.getAttribute("loginMember");

        if (memberUid == null ||
                memberUid.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "로그인 후 문의글을 작성할 수 있습니다."
            );

            return "redirect:/member/login";
        }

        QnaDTO qna =
                new QnaDTO();

        qna.setMemberUid(memberUid);

        model.addAttribute(
                "qna",
                qna
        );

        return "cs/qnaWrite";
    }

    /* 문의글 등록 */
    @PostMapping("/qnaWrite")
    public String qnaWrite(
            QnaDTO dto,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String memberUid =
                (String) session.getAttribute("loginMember");

        if (memberUid == null ||
                memberUid.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "로그인 후 문의글을 작성할 수 있습니다."
            );

            return "redirect:/member/login";
        }

        dto.setMemberUid(memberUid);

        qnaService.insertQna(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "문의글이 등록되었습니다."
        );

        return "redirect:/cs/qnaList";
    }

    /* 문자열 공백 정리 */
    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim();
    }
}