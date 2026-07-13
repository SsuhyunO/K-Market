package org.example.k_market.controller.member;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 구글 소셜 로그인으로 "최초 가입"한 회원의 추가정보(생년월일/성별/전화번호/주소) 입력을 처리하는 컨트롤러.
 *
 * 흐름:
 * 1) 구글 로그인 성공 시 회원 status가 PENDING이면 OAuth2LoginSuccessHandler가
 *    세션에 pendingGoogleUid만 세팅하고 이 컨트롤러의 /additional-info로 리다이렉트시킴
 * 2) 사용자가 폼 입력 후 확인 버튼 클릭 -> /complete-profile 로 저장 요청
 * 3) 저장 성공 시 status를 ACTIVE로 바꾸고, 그제서야 정식 로그인 세션(loginMember)을 세팅
 *    -> 이후 메인페이지로 이동하면 헤더에 "OOO님 환영합니다"가 정상적으로 뜸
 */
@Controller
@RequestMapping("/member/google")
@RequiredArgsConstructor
public class GoogleProfileController {

    private final MemberRepository memberRepository;

    // 추가정보 입력 폼 페이지
    @GetMapping("/additional-info")
    public String additionalInfoPage(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("pendingGoogleUid");
        if (uid == null) {
            // 정상적인 흐름이 아니면 로그인 페이지로
            return "redirect:/member/login";
        }

        Member member = memberRepository.findById(uid).orElse(null);
        if (member == null || !member.isProfilePending()) {
            // 이미 완료됐거나 잘못된 접근이면 메인으로
            return "redirect:/";
        }

        model.addAttribute("member", member);
        // 아래 뷰 이름(경로)은 프로젝트의 register.html 위치에 맞춰 조정하세요.
        // 예: templates/member/register.html 과 같은 위치라면 "member/register-google"
        return "member/register-google";
    }

    // 추가정보 저장 처리
    @PostMapping("/complete-profile")
    @ResponseBody
    public Map<String, Object> completeProfile(@RequestBody GoogleProfileRequest request, HttpSession session) {
        String uid = (String) session.getAttribute("pendingGoogleUid");
        if (uid == null) {
            return Map.of("success", false, "message", "잘못된 접근입니다. 다시 로그인해주세요.");
        }

        Member member = memberRepository.findById(uid).orElse(null);
        if (member == null) {
            return Map.of("success", false, "message", "존재하지 않는 회원입니다.");
        }

        member.completeGoogleProfile(
                request.getName(),
                request.getBirthDate(),
                request.getGender(),
                request.getPhone(),
                request.getZipCode(),
                request.getAddr1(),
                request.getAddr2()
        );
        memberRepository.save(member);

        // ===== 추가정보 입력 완료 -> 이제서야 정식 로그인 처리 =====
        session.removeAttribute("pendingGoogleUid");
        session.setAttribute("loginMember", member.getUid());
        session.setAttribute("loginMemberType", member.getMemberType());

        return Map.of("success", true);
    }

    @Getter
    @NoArgsConstructor
    public static class GoogleProfileRequest {
        private String name;
        private String birthDate;
        private String gender;
        private String phone;
        private String zipCode;
        private String addr1;
        private String addr2;
    }
}