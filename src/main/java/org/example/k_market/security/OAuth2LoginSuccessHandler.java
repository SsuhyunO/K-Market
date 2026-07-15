package org.example.k_market.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isEmpty()) {
            // 정상적인 흐름에서는 CustomOAuth2UserService에서 이미 생성/조회 되었어야 함
            response.sendRedirect(request.getContextPath() + "/member/login?error=true");
            return;
        }

        Member member = memberOpt.get();

        if (member.isWithdrawn()) {
            response.sendRedirect(request.getContextPath() + "/member/login?withdrawn=true");
            return;
        }

        HttpSession session = request.getSession();

        // ===== 구글 최초가입 -> 추가정보(생년월일/성별/전화번호/주소) 입력 전 상태 =====
        // 아직 정식 로그인 세션(loginMember)은 세팅하지 않는다.
        // -> 헤더에 "OOO님 환영합니다"가 뜨지 않아야 하기 때문 (추가정보 입력 완료 후에 로그인 처리됨)
        if (member.isProfilePending()) {
            session.setAttribute("pendingGoogleUid", member.getUid());
            response.sendRedirect(request.getContextPath() + "/member/google/additional-info");
            return;
        }

        // ===== 이미 가입 완료된 회원 (재로그인) -> 기존 일반 로그인과 동일한 방식으로 세션 세팅 =====
        member.updateLastLoginAt();
        memberRepository.save(member);

        // 중요: 반드시 uid(String)만 세션에 저장한다.
        // (기존 버그: member 객체 전체를 저장해서 /api/member/me 에서
        //  (String) session.getAttribute("loginMember") 캐스팅 시 ClassCastException 발생 -> 헤더 표시 안 됨)
        session.setAttribute("loginMember", member.getUid());
        session.setAttribute("loginMemberType", member.getMemberType());
        session.setAttribute("loginMemberLevel", member.getMemberLevel());

        response.sendRedirect(request.getContextPath() + "/");
    }
}
