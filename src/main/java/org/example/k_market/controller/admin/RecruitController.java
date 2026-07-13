package org.example.k_market.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.RecruitDTO;
import org.example.k_market.service.admin.RecruitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecruitController {

    private final RecruitService recruitService;


    // =========================================================
    // 채용공고 목록 및 검색 + 페이지네이션
    // =========================================================

    @GetMapping("/admin/cs/recruit/list")
    public String list(
            @RequestParam(
                    name = "searchType",
                    defaultValue = "id"
            ) String searchType,

            @RequestParam(
                    name = "keyword",
                    required = false
            ) String keyword,

            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,

            @RequestParam(
                    name = "size",
                    defaultValue = "10"
            ) int size,

            Model model
    ) {

        String safeSearchType =
                searchType == null || searchType.isBlank()
                        ? "id"
                        : searchType.trim();

        String safeKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        // 화면에서는 페이지 번호를 1부터 사용
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        // Spring Data JPA 페이지 번호는 0부터 시작
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "id"
                )
        );

        Page<RecruitDTO> recruitPage =
                recruitService.findRecruits(
                        safeSearchType,
                        safeKeyword,
                        pageable
                );

        int currentPage =
                recruitPage.getNumber() + 1;

        int totalPages =
                recruitPage.getTotalPages();

        // 페이지 번호를 한 화면에 5개씩 표시
        int pageGroupSize = 5;

        int startPage =
                ((currentPage - 1) / pageGroupSize)
                        * pageGroupSize + 1;

        int endPage =
                Math.min(
                        startPage + pageGroupSize - 1,
                        totalPages
                );

        /*
         * 기존 HTML이 ${dtoList}를 사용하고 있으므로
         * 이름은 dtoList로 유지
         */
        model.addAttribute(
                "dtoList",
                recruitPage.getContent()
        );

        model.addAttribute(
                "recruitPage",
                recruitPage
        );

        model.addAttribute(
                "currentPage",
                currentPage
        );

        model.addAttribute(
                "totalPages",
                totalPages
        );

        model.addAttribute(
                "startPage",
                startPage
        );

        model.addAttribute(
                "endPage",
                endPage
        );

        model.addAttribute(
                "size",
                size
        );

        // 검색 후 선택한 검색 조건 유지
        model.addAttribute(
                "searchType",
                safeSearchType
        );

        // 검색 후 입력한 검색어 유지
        model.addAttribute(
                "keyword",
                safeKeyword
        );

        return "admin/cs/recruit/list";
    }


    // =========================================================
    // 채용공고 등록
    // =========================================================

    @PostMapping("/admin/cs/recruit/register")
    public String register(
            RecruitDTO dto,

            @RequestParam(required = false)
            String startDate,

            @RequestParam(required = false)
            String endDate,

            @RequestParam(required = false)
            String startTime,

            @RequestParam(required = false)
            String endTime,

            RedirectAttributes redirectAttributes
    ) {

        dto.setRecruitStartAt(
                toLocalDateTime(
                        startDate,
                        startTime
                )
        );

        dto.setRecruitEndAt(
                toLocalDateTime(
                        endDate,
                        endTime
                )
        );

        if (dto.getSellerUid() == null
                || dto.getSellerUid().isBlank()) {

            dto.setSellerUid("관리자");
        }

        if (dto.getStatus() == null
                || dto.getStatus().isBlank()) {

            dto.setStatus("모집중");
        }

        dto.setCreatedAt(
                LocalDateTime.now()
        );

        recruitService.register(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "채용공고가 등록되었습니다."
        );

        return "redirect:/admin/cs/recruit/list";
    }


    // =========================================================
    // 채용공고 선택 삭제
    // =========================================================

    @PostMapping("/admin/cs/recruit/delete")
    public String deleteSelected(
            @RequestParam(
                    name = "recruitNo",
                    required = false
            ) List<Integer> recruitNo,

            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,

            @RequestParam(
                    name = "searchType",
                    defaultValue = "id"
            ) String searchType,

            @RequestParam(
                    name = "keyword",
                    required = false
            ) String keyword,

            RedirectAttributes redirectAttributes
    ) {

        if (recruitNo == null || recruitNo.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "삭제할 채용공고를 선택해주세요."
            );

            redirectAttributes.addAttribute(
                    "page",
                    page
            );

            redirectAttributes.addAttribute(
                    "searchType",
                    searchType
            );

            if (keyword != null && !keyword.isBlank()) {
                redirectAttributes.addAttribute(
                        "keyword",
                        keyword
                );
            }

            return "redirect:/admin/cs/recruit/list";
        }

        recruitService.deleteSelected(recruitNo);

        redirectAttributes.addFlashAttribute(
                "message",
                "선택한 채용공고가 삭제되었습니다."
        );

        /*
         * 삭제 후 기존 페이지와 검색 조건 유지
         */
        redirectAttributes.addAttribute(
                "page",
                page
        );

        redirectAttributes.addAttribute(
                "searchType",
                searchType
        );

        if (keyword != null && !keyword.isBlank()) {
            redirectAttributes.addAttribute(
                    "keyword",
                    keyword
            );
        }

        return "redirect:/admin/cs/recruit/list";
    }


    // =========================================================
    // 채용공고 수정 화면
    // =========================================================

    @GetMapping("/admin/cs/recruit/modify")
    public String modifyForm(
            @RequestParam("id") Integer id,
            Model model
    ) {

        RecruitDTO recruitDTO =
                recruitService.findById(id);

        model.addAttribute(
                "recruit",
                recruitDTO
        );

        return "admin/cs/recruit/modify";
    }


    // =========================================================
    // 채용공고 수정 처리
    // =========================================================

    @PostMapping("/admin/cs/recruit/modify")
    public String modify(
            RecruitDTO dto,

            @RequestParam(required = false)
            String startDate,

            @RequestParam(required = false)
            String endDate,

            @RequestParam(required = false)
            String startTime,

            @RequestParam(required = false)
            String endTime,

            RedirectAttributes redirectAttributes
    ) {

        dto.setRecruitStartAt(
                toLocalDateTime(
                        startDate,
                        startTime
                )
        );

        dto.setRecruitEndAt(
                toLocalDateTime(
                        endDate,
                        endTime
                )
        );

        if (dto.getSellerUid() == null
                || dto.getSellerUid().isBlank()) {

            dto.setSellerUid("관리자");
        }

        if (dto.getStatus() == null
                || dto.getStatus().isBlank()) {

            dto.setStatus("모집중");
        }

        recruitService.modify(dto);

        redirectAttributes.addFlashAttribute(
                "message",
                "채용공고가 수정되었습니다."
        );

        return "redirect:/admin/cs/recruit/list";
    }


    // =========================================================
    // 날짜 + 시간 변환
    // =========================================================

    private LocalDateTime toLocalDateTime(
            String date,
            String time
    ) {

        if (date == null || date.isBlank()) {
            return null;
        }

        try {

            LocalDate localDate =
                    LocalDate.parse(date);

            LocalTime localTime =
                    time == null || time.isBlank()
                            ? LocalTime.of(0, 0)
                            : LocalTime.parse(time);

            return LocalDateTime.of(
                    localDate,
                    localTime
            );

        } catch (Exception e) {

            System.out.println(
                    "날짜 파싱 중 에러 발생: "
                            + e.getMessage()
            );

            return null;
        }
    }
}