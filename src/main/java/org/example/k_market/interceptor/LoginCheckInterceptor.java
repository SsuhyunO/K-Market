package org.example.k_market.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Object loginMember = (session != null) ? session.getAttribute("loginMember") : null;

        if (loginMember == null) {
            response.sendRedirect(request.getContextPath() + "/member/login?loginRequired=true");
            return false;
        }
        return true;
    }
}