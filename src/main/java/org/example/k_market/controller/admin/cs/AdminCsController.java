package org.example.k_market.controller.admin.cs;

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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller("adminCsController")
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
public class AdminCsController {

    private final NoticeService noticeService;
    private final FaqService faqService;
    private final QnaService qnaService;

    /* 공지사항 목록 조회 */
    @GetMapping({"", "/", "/notice", "/notice/list"})
    public String noticeList(
            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,
            Model model
    ) {
        int safePage = Math.max(page, 1);

        model.addAttribute(
                "noticeList",
                noticeService.getNoticeList(safePage)
        );

        model.addAttribute(
                "pageInfo",
                noticeService.getPageInfo(safePage)
        );

        return "admin/cs/notice/list";
    }

    /* 공지사항 상세 조회 */
    @GetMapping("/notice/view")
    public String noticeView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        NoticeDTO notice =
                noticeService.getNotice(boardNo);

        model.addAttribute(
                "notice",
                notice
        );

        return "admin/cs/notice/view";
    }

    /* 공지사항 작성 화면 */
    @GetMapping("/notice/write")
    public String noticeWrite() {
        return "admin/cs/notice/write";
    }

    /* 공지사항 등록 */
    @PostMapping("/notice/write")
    public String noticeWrite(
            NoticeDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        noticeService.insertNotice(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "공지사항이 등록되었습니다."
        );

        return "redirect:/admin/cs/notice/list";
    }

    /* 공지사항 수정 화면 */
    @GetMapping("/notice/modify")
    public String noticeModify(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        NoticeDTO notice =
                noticeService.getNotice(boardNo);

        model.addAttribute(
                "notice",
                notice
        );

        return "admin/cs/notice/modify";
    }

    /* 공지사항 수정 */
    @PostMapping("/notice/modify")
    public String noticeModify(
            NoticeDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        noticeService.updateNotice(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "공지사항이 수정되었습니다."
        );

        return "redirect:/admin/cs/notice/view?boardNo="
                + dto.getBoardNo();
    }

    /* 공지사항 단일 및 선택 삭제 */
    @PostMapping("/notice/delete")
    public String noticeDelete(
            @RequestParam(
                    name = "boardNo",
                    required = false
            ) List<Integer> boardNoList,
            RedirectAttributes redirectAttributes
    ) {
        if (boardNoList == null ||
                boardNoList.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "삭제할 공지사항을 선택해주세요."
            );

            return "redirect:/admin/cs/notice/list";
        }

        noticeService.deleteNotices(boardNoList);

        redirectAttributes.addFlashAttribute(
                "message",
                "공지사항이 삭제되었습니다."
        );

        return "redirect:/admin/cs/notice/list";
    }

    /* FAQ 목록 및 유형별 조회 */
    @GetMapping({"/faq", "/faq/list"})
    public String faqList(
            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,
            @RequestParam(
                    name = "category1",
                    required = false
            ) String category1,
            Model model
    ) {
        int safePage = Math.max(page, 1);

        String selectedCategory1 =
                normalize(category1);

        model.addAttribute(
                "faqList",
                faqService.getFaqList(
                        safePage,
                        selectedCategory1
                )
        );

        model.addAttribute(
                "pageInfo",
                faqService.getPageInfo(
                        safePage,
                        selectedCategory1
                )
        );

        model.addAttribute(
                "selectedCategory1",
                selectedCategory1
        );

        String faqPageUrl =
                createFaqPageUrl(selectedCategory1);

        model.addAttribute(
                "faqPageUrl",
                faqPageUrl
        );

        return "admin/cs/faq/list";
    }

    /* FAQ 상세 조회 */
    @GetMapping("/faq/view")
    public String faqView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        FaqDTO faq =
                faqService.getFaq(boardNo);

        model.addAttribute(
                "faq",
                faq
        );

        return "admin/cs/faq/view";
    }

    /* FAQ 작성 화면 */
    @GetMapping("/faq/write")
    public String faqWrite() {
        return "admin/cs/faq/write";
    }

    /* FAQ 등록 */
    @PostMapping("/faq/write")
    public String faqWrite(
            FaqDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        faqService.insertFaq(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "자주 묻는 질문이 등록되었습니다."
        );

        return "redirect:/admin/cs/faq/list";
    }

    /* FAQ 수정 화면 */
    @GetMapping("/faq/modify")
    public String faqModify(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        FaqDTO faq =
                faqService.getFaq(boardNo);

        model.addAttribute(
                "faq",
                faq
        );

        return "admin/cs/faq/modify";
    }

    /* FAQ 수정 */
    @PostMapping("/faq/modify")
    public String faqModify(
            FaqDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        faqService.updateFaq(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "자주 묻는 질문이 수정되었습니다."
        );

        return "redirect:/admin/cs/faq/view?boardNo="
                + dto.getBoardNo();
    }

    /* FAQ 단일 및 선택 삭제 */
    @PostMapping("/faq/delete")
    public String faqDelete(
            @RequestParam(
                    name = "boardNo",
                    required = false
            ) List<Integer> boardNoList,
            RedirectAttributes redirectAttributes
    ) {
        if (boardNoList == null ||
                boardNoList.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "삭제할 자주 묻는 질문을 선택해주세요."
            );

            return "redirect:/admin/cs/faq/list";
        }

        faqService.deleteFaqs(boardNoList);

        redirectAttributes.addFlashAttribute(
                "message",
                "자주 묻는 질문이 삭제되었습니다."
        );

        return "redirect:/admin/cs/faq/list";
    }

    /* 문의글 목록 및 유형별 조회 */
    @GetMapping({"/qna", "/qna/list"})
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

        String qnaPageUrl =
                createQnaPageUrl(
                        selectedCategory1,
                        selectedCategory2
                );

        model.addAttribute(
                "qnaPageUrl",
                qnaPageUrl
        );

        return "admin/cs/qna/list";
    }

    /* 문의글 상세 조회 */
    @GetMapping("/qna/view")
    public String qnaView(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        QnaDTO qna =
                qnaService.getQna(boardNo);

        model.addAttribute(
                "qna",
                qna
        );

        return "admin/cs/qna/view";
    }

    /* 문의 답변 작성 화면 */
    @GetMapping("/qna/reply")
    public String qnaReply(
            @RequestParam("boardNo") int boardNo,
            Model model
    ) {
        QnaDTO qna =
                qnaService.getQna(boardNo);

        model.addAttribute(
                "qna",
                qna
        );

        return "admin/cs/qna/reply";
    }

    /* 문의 답변 등록 및 수정 */
    @PostMapping("/qna/reply")
    public String qnaReply(
            @RequestParam("boardNo") int boardNo,
            @RequestParam("answer") String answer,
            RedirectAttributes redirectAttributes
    ) {
        QnaDTO qna =
                qnaService.getQna(boardNo);

        boolean alreadyAnswered =
                qna.isAnswered();

        qnaService.updateAnswer(
                boardNo,
                answer
        );

        redirectAttributes.addFlashAttribute(
                "message",
                alreadyAnswered
                        ? "문의 답변이 수정되었습니다."
                        : "문의 답변이 등록되었습니다."
        );

        return "redirect:/admin/cs/qna/view?boardNo="
                + boardNo;
    }

    /* 문의 답변 삭제 */
    @PostMapping("/qna/reply/delete")
    public String qnaReplyDelete(
            @RequestParam("boardNo") int boardNo,
            RedirectAttributes redirectAttributes
    ) {
        qnaService.deleteAnswer(boardNo);

        redirectAttributes.addFlashAttribute(
                "message",
                "문의 답변이 삭제되었습니다."
        );

        return "redirect:/admin/cs/qna/view?boardNo="
                + boardNo;
    }

    /* 문의글 단일 및 선택 삭제 */
    @PostMapping("/qna/delete")
    public String qnaDelete(
            @RequestParam(
                    name = "boardNo",
                    required = false
            ) List<Integer> boardNoList,
            RedirectAttributes redirectAttributes
    ) {
        if (boardNoList == null ||
                boardNoList.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "삭제할 문의글을 선택해주세요."
            );

            return "redirect:/admin/cs/qna/list";
        }

        qnaService.deleteQnas(boardNoList);

        redirectAttributes.addFlashAttribute(
                "message",
                "문의글이 삭제되었습니다."
        );

        return "redirect:/admin/cs/qna/list";
    }

    /* 문자열 공백 제거 */
    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim();
    }

    /* FAQ 페이지 주소 생성 */
    private String createFaqPageUrl(
            String category1
    ) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromPath(
                        "/admin/cs/faq/list"
                );

        if (!category1.isBlank()) {
            builder.queryParam(
                    "category1",
                    category1
            );
        }

        return builder
                .build()
                .encode()
                .toUriString();
    }

    /* 문의글 페이지 주소 생성 */
    private String createQnaPageUrl(
            String category1,
            String category2
    ) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromPath(
                        "/admin/cs/qna/list"
                );

        if (!category1.isBlank()) {
            builder.queryParam(
                    "category1",
                    category1
            );
        }

        if (!category2.isBlank()) {
            builder.queryParam(
                    "category2",
                    category2
            );
        }

        return builder
                .build()
                .encode()
                .toUriString();
    }
}