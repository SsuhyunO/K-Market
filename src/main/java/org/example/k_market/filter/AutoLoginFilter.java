package org.example.k_market.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.Member;
import org.example.k_market.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ===== 자동로그인 필터 =====
// 세션이 없을 때, 쿠키에 담긴 autoLoginToken이 유효하면 자동으로 세션을 재생성해서 로그인 상태로 만들어준다.
// 인터셉터(LoginCheckInterceptor)보다 항상 먼저 동작하므로, 세션 체크가 이루어지기 전에 자동 로그인이 완료된다.
@Component
@RequiredArgsConstructor
public class AutoLoginFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "autoLoginToken";

    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        boolean alreadyLoggedIn = (session != null && session.getAttribute("loginMember") != null);

        if (!alreadyLoggedIn) {
            String token = getCookieValue(request, COOKIE_NAME);
            if (token != null) {
                Member member = memberService.findByValidAutoLoginToken(token);
                if (member != null && !member.isWithdrawn()) {
                    request.getSession(true).setAttribute("loginMember", member.getUid());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}