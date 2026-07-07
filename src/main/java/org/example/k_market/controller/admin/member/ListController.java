package org.example.k_market.controller.admin.member;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.service.admin.AdminMemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller("memberListController")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ListController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/member-list")
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<MemberDto.AdminListItem> memberPage =
                adminMemberService.searchMemberList(searchType, keyword, pageable);

        model.addAttribute("memberList", memberPage.getContent());
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "admin/member/list";
    }

    // ===== 회원정보 수정 (모달 "수정하기" 버튼) =====
    @PostMapping("/member-update")
    @ResponseBody
    public ResponseEntity<String> updateMember(@RequestBody MemberDto.AdminUpdateRequest request) {
        adminMemberService.updateMember(request);
        return ResponseEntity.ok("수정되었습니다.");
    }

    // ===== 등급 즉시변경 (목록 행 select box) =====
    @PostMapping("/member-grade-update")
    @ResponseBody
    public ResponseEntity<String> updateGrade(@RequestBody MemberDto.AdminGradeUpdateRequest request) {
        adminMemberService.updateGrade(request.getUid(), request.getMemberLevel());
        return ResponseEntity.ok("등급이 변경되었습니다.");
    }
}