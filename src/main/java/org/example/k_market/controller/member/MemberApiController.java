package org.example.k_market.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.entity.Member;
import org.example.k_market.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복확인 -> true면 이미 사용중(중복), false면 사용가능
    @GetMapping("/check-uid")
    public boolean checkUid(@RequestParam String uid) {
        return memberService.isUidDuplicate(uid);
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
    public MemberDto.Response login(@RequestBody MemberDto.LoginRequest request, HttpSession session) {
        Member member = memberService.findByUid(request.getUid());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("loginMember", member.getUid());
        return MemberDto.Response.from(member);
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "로그아웃 되었습니다.";
    }

    // 현재 로그인한 회원 정보 조회 ("OO님 환영합니다" 띄울 때 사용)
    @GetMapping("/me")
    public MemberDto.Response getMyInfo(HttpSession session) {
        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            return null;
        }
        Member member = memberService.findByUid(uid);
        return MemberDto.Response.from(member);
    }

    // 아이디 찾기
    @PostMapping("/find-uid")
    public String findUid(@RequestBody MemberDto.FindUidRequest request) {
        return memberService.findUid(request);
    }

    // 비밀번호 찾기 - 본인확인
    @PostMapping("/find-password")
    public boolean findPassword(@RequestBody MemberDto.FindPasswordRequest request) {
        return memberService.verifyForPasswordReset(request);
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody MemberDto.ResetPasswordRequest request) {
        memberService.resetPassword(request);
        return "비밀번호가 변경되었습니다.";
    }
}