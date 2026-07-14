package org.example.k_market.controller.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.entity.Member;
import org.example.k_market.service.MemberService;
import org.example.k_market.service.EmailAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService;

    private static final String AUTO_LOGIN_COOKIE = "autoLoginToken";

    // 이메일 인증번호 발송
    @PostMapping("/email/send-code")
    public String sendEmailCode(@RequestBody MemberDto.EmailAuthRequest request) {
        emailAuthService.sendCode(request.getEmail());
        return "인증번호가 발송되었습니다.";
    }

    // 이메일 인증번호 확인
    @PostMapping("/email/verify-code")
    public boolean verifyEmailCode(@RequestBody MemberDto.EmailAuthVerifyRequest request) {
        return emailAuthService.verifyCode(request.getEmail(), request.getAuthCode());
    }

    // 아이디 중복확인 -> true면 이미 사용중(중복), false면 사용가능
    @GetMapping("/check-uid")
    public boolean checkUid(@RequestParam String uid) {
        return memberService.isUidDuplicate(uid);
    }

    // 이메일 중복확인 -> true면 이미 사용중(중복), false면 사용가능
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return memberService.isEmailDuplicate(email);
    }

    // 회원가입
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody MemberDto.SignUpRequest request, HttpServletRequest httpRequest) {
        String regIp = httpRequest.getRemoteAddr();
        memberService.signUp(request, regIp);
        return "회원가입이 완료되었습니다.";
    }

    // 로그인
    @PostMapping("/login")
    public MemberDto.Response login(@RequestBody MemberDto.LoginRequest request, HttpSession session,
                                    HttpServletResponse response) {
        Member member = memberService.findByUid(request.getUid());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 탈퇴한 계정은 로그인 자체를 차단
        if (member.isWithdrawn()) {
            throw new IllegalStateException("탈퇴한 계정입니다. 재로그인이 불가능합니다.");
        }

        session.setAttribute("loginMember", member.getUid());
        // ===== 추가된 부분: 역할(권한) 기반 화면/접근 제어를 위해 memberType도 세션에 저장 =====
        session.setAttribute("loginMemberType", member.getMemberType()); // "MEMBER" / "SELLER" / "ADMIN"
        session.setAttribute("loginMemberLevel", member.getMemberLevel());

        // ===== 추가된 부분: 최근 로그인 시각 갱신 =====
        memberService.updateLastLoginAt(member.getUid());

        // 자동로그인 체크 시 토큰 발급 + 쿠키 저장 (7일)
        if (request.isAutoLogin()) {
            String token = memberService.issueAutoLoginToken(member.getUid());
            Cookie cookie = new Cookie(AUTO_LOGIN_COOKIE, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
            response.addCookie(cookie);
        }

        return MemberDto.Response.from(member);
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // 자동로그인 토큰도 같이 제거 (안 하면 로그아웃해도 다시 자동 로그인됨)
        String uid = (String) session.getAttribute("loginMember");
        if (uid != null) {
            memberService.clearAutoLoginToken(uid);
        }
        clearAutoLoginCookie(response);

        session.invalidate();
        return "로그아웃 되었습니다.";
    }

    // 현재 로그인한 회원 정보 조회 ("OO님 환영합니다" 띄울 때 사용, 마이페이지 조회에도 사용)
    @GetMapping("/me")
    public MemberDto.Response getMyInfo(HttpSession session) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            return null;
        }
        Member member = memberService.findByUid(uid);
        session.setAttribute("loginMemberType", member.getMemberType());
        return MemberDto.Response.from(member);
    }

    // 아이디 찾기
    @PostMapping("/find-uid")
    public MemberDto.FindUidResult findUid(@RequestBody MemberDto.FindUidRequest request) {
        return memberService.findUid(request);
    }

    // 비밀번호 찾기 - 본인확인
    @PostMapping("/find-password")
    public boolean findPassword(@RequestBody MemberDto.FindPasswordRequest request) {
        return memberService.verifyForPasswordReset(request);
    }

    // 비밀번호 재설정 (아이디/이메일 찾기 흐름에서 사용하는 기존 기능)
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody MemberDto.ResetPasswordRequest request) {
        memberService.resetPassword(request);
        return "비밀번호가 변경되었습니다.";
    }

    // 마이페이지 - 현재 비밀번호 확인 (팝업 1단계)
    @PostMapping("/mypage/password/verify")
    public Map<String, Object> verifyCurrentPassword(@RequestParam String password, HttpSession session) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        boolean matched = memberService.verifyCurrentPassword(uid, password);
        return Map.of("success", matched);
    }

    // 마이페이지 - 비밀번호 변경 (팝업 2단계)
    @PostMapping("/mypage/password/change")
    public Map<String, Object> changeMyPassword(@RequestParam String newPassword,
                                                @RequestParam String newPasswordConfirm,
                                                HttpSession session) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            return Map.of("success", false, "message", "새 비밀번호가 일치하지 않습니다.");
        }
        memberService.changePasswordByUid(uid, newPassword);
        return Map.of("success", true);
    }

    // 마이페이지 정보수정 (휴대폰/주소) - 이메일은 여기서도 무시됨
    @PostMapping("/mypage/update")
    public String updateProfile(@RequestBody MemberDto.UpdateRequest request, HttpSession session) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        memberService.updateProfile(uid, request);
        return "수정이 완료되었습니다.";
    }

    // 탈퇴
    @PostMapping("/withdraw")
    public String withdraw(HttpSession session, HttpServletResponse response) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        memberService.withdraw(uid);
        // 탈퇴 시 자동로그인 토큰/쿠키도 정리
        memberService.clearAutoLoginToken(uid);
        clearAutoLoginCookie(response);

        session.invalidate(); // 탈퇴 즉시 세션 종료 -> 이후 어떤 요청도 로그인 상태로 인식되지 않음
        return "탈퇴가 완료되었습니다.";
    }

    // 자동로그인 쿠키 삭제 헬퍼
    private void clearAutoLoginCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTO_LOGIN_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}