package org.example.k_market.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.Member;
import org.example.k_market.service.MemberService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * /admin/** 경로 접근 권한을 체크하는 인터셉터.
 *
 * - 비로그인 -> 로그인 페이지로 이동
 * - memberType == "ADMIN" -> 전체 admin 페이지 접근 가능
 * - memberType == "SELLER" -> 아래 SELLER_ALLOWED_PREFIXES 에 해당하는 경로만 접근 가능
 * - memberType == "MEMBER"(일반회원) -> admin 접근 자체 차단
 *
 * 기존 LoginCheckInterceptor(단순 로그인 여부 체크)는 그대로 두고,
 * admin 전용 권한 체크만 이 인터셉터에서 별도로 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class AdminRoleInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    // 판매자(SELLER)에게 허용되는 admin 경로 prefix
    // aside.html 의 "상점관리 / 상품관리 / 주문관리 / 쿠폰관리" 메뉴와 동일하게 맞춤
    private static final Set<String> SELLER_ALLOWED_PREFIXES = Set.of(
            "/admin/main",
            "/admin/product",
            "/admin/order",
            "/admin/coupon"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String memberType = resolveMemberType(session);

        // 비로그인 상태
        if (memberType == null) {
            response.sendRedirect(request.getContextPath() + "/member/login?loginRequired=true");
            return false;
        }

        // 관리자는 전체 admin 페이지 접근 가능
        if ("ADMIN".equals(memberType)) {
            return true;
        }

        // 판매자는 허용된 경로만 접근 가능
        if ("SELLER".equals(memberType)) {
            String servletPath = request.getServletPath();
            boolean allowed = SELLER_ALLOWED_PREFIXES.stream().anyMatch(servletPath::startsWith);
            if (allowed) {
                return true;
            }
            response.sendRedirect(request.getContextPath() + "/admin/product/list?accessDenied=true");
            return false;
        }

        // 일반 회원(MEMBER)은 admin 접근 자체 차단
        response.sendRedirect(request.getContextPath() + "/?accessDenied=true");
        return false;
    }

    private String resolveMemberType(HttpSession session) {
        if (session == null) {
            return null;
        }

        String memberType = (String) session.getAttribute("loginMemberType");
        if (memberType != null) {
            return memberType;
        }

        String uid = (String) session.getAttribute("loginMember");
        if (uid == null) {
            return null;
        }

        try {
            Member member = memberService.findByUid(uid);
            memberType = member.getMemberType();
            session.setAttribute("loginMemberType", memberType);
            return memberType;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
