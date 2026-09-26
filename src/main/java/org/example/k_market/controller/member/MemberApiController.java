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
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final EmailAuthService emailAuthService;

    private static final String AUTO_LOGIN_COOKIE = "autoLoginToken";
    private static final String VERIFIED_EMAIL = "verifiedEmail";
    private static final String VERIFIED_EMAIL_AT = "verifiedEmailAt";
    private static final String PASSWORD_RESET_UID = "passwordResetUid";
    private static final String PASSWORD_RESET_AT = "passwordResetAt";
    private static final long VERIFICATION_VALID_MINUTES = 10;

    // 이메일 인증번호 발송
    @PostMapping("/email/send-code")
    public String sendEmailCode(@Valid @RequestBody MemberDto.EmailAuthRequest request) {
        emailAuthService.sendCode(request.getEmail());
        return "인증번호가 발송되었습니다.";
    }

    // 이메일 인증번호 확인
    @PostMapping("/email/verify-code")
    public boolean verifyEmailCode(@Valid @RequestBody MemberDto.EmailAuthVerifyRequest request,
                                   HttpSession session) {
        boolean verified = emailAuthService.verifyCode(request.getEmail(), request.getAuthCode());
        if (verified) {
            session.setAttribute(VERIFIED_EMAIL, normalizeEmail(request.getEmail()));
            session.setAttribute(VERIFIED_EMAIL_AT, Instant.now());
        }
        return verified;
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
    public String signup(@Valid @RequestBody MemberDto.SignUpRequest request,
                         HttpServletRequest httpRequest,
                         HttpSession session) {
        requireVerifiedEmail(session, request.getEmail());
        String regIp = httpRequest.getRemoteAddr();
        List<String> issuedCouponNames = memberService.signUp(request, regIp);
        clearVerifiedEmail(session);
        return !issuedCouponNames.isEmpty()
                ? "회원가입이 완료되었습니다. " + formatCouponNames(issuedCouponNames) + "이 지급되었습니다."
                : "회원가입이 완료되었습니다.";
    }

    // 로그인
    @PostMapping("/login")
    public MemberDto.Response login(@RequestBody MemberDto.LoginRequest request, HttpSession session,
                                    HttpServletResponse response) {
        Member member = memberService.authenticate(request.getUid(), request.getPassword());

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
    public MemberDto.FindUidResult findUid(@Valid @RequestBody MemberDto.FindUidRequest request,
                                           HttpSession session) {
        requireVerifiedEmail(session, request.getEmail());
        MemberDto.FindUidResult result = memberService.findUid(request);
        clearVerifiedEmail(session);
        return result;
    }

    // 비밀번호 찾기 - 본인확인
    @PostMapping("/find-password")
    public boolean findPassword(@Valid @RequestBody MemberDto.FindPasswordRequest request,
                                HttpSession session) {
        requireVerifiedEmail(session, request.getEmail());
        boolean matched = memberService.verifyForPasswordReset(request);
        if (matched) {
            session.setAttribute(PASSWORD_RESET_UID, request.getUid());
            session.setAttribute(PASSWORD_RESET_AT, Instant.now());
            clearVerifiedEmail(session);
        }
        return matched;
    }

    // 비밀번호 재설정 (아이디/이메일 찾기 흐름에서 사용하는 기존 기능)
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody MemberDto.ResetPasswordRequest request,
                                HttpSession session) {
        String authorizedUid = (String) session.getAttribute(PASSWORD_RESET_UID);
        Instant authorizedAt = (Instant) session.getAttribute(PASSWORD_RESET_AT);
        if (!request.getUid().equals(authorizedUid)
                || authorizedAt == null
                || authorizedAt.isBefore(Instant.now().minus(VERIFICATION_VALID_MINUTES, ChronoUnit.MINUTES))) {
            clearPasswordResetAuthorization(session);
            throw new IllegalStateException("비밀번호 재설정 인증이 만료되었습니다. 이메일 인증을 다시 진행해주세요.");
        }
        memberService.resetPassword(request);
        clearPasswordResetAuthorization(session);
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

    private String formatCouponNames(List<String> couponNames) {
        return couponNames.stream()
                .map(name -> "‘" + name + "’")
                .reduce((left, right) -> left + ", " + right)
                .orElse("쿠폰");
    }

    private void requireVerifiedEmail(HttpSession session, String email) {
        String verifiedEmail = (String) session.getAttribute(VERIFIED_EMAIL);
        Instant verifiedAt = (Instant) session.getAttribute(VERIFIED_EMAIL_AT);
        if (!normalizeEmail(email).equals(verifiedEmail)
                || verifiedAt == null
                || verifiedAt.isBefore(Instant.now().minus(VERIFICATION_VALID_MINUTES, ChronoUnit.MINUTES))) {
            clearVerifiedEmail(session);
            throw new IllegalStateException("이메일 인증이 필요하거나 인증 시간이 만료되었습니다.");
        }
    }

    private void clearVerifiedEmail(HttpSession session) {
        session.removeAttribute(VERIFIED_EMAIL);
        session.removeAttribute(VERIFIED_EMAIL_AT);
    }

    private void clearPasswordResetAuthorization(HttpSession session) {
        session.removeAttribute(PASSWORD_RESET_UID);
        session.removeAttribute(PASSWORD_RESET_AT);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
