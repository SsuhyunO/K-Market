package org.example.k_market.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.example.k_market.common.admin.AdminCategory;
import org.springframework.http.HttpStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        String path = getOriginalPath(request);
        int statusCode = getStatusCode(request);
        String title = getTitle(statusCode);
        String message = getMessage(statusCode);

        if (path.equals("/admin") || path.startsWith("/admin/")) {
            model.addAttribute("category", AdminCategory.ADMIN_ERROR);
            model.addAttribute("requestedPath", path);
            model.addAttribute("errorCode", statusCode);
            model.addAttribute("errorTitle", title);
            model.addAttribute("errorMessage", message);
            return "admin/error/404";
        }

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        return "error/404";
    }

    private int getStatusCode(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            return code;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String getTitle(int statusCode) {
        return switch (statusCode) {
            case 400 -> "잘못된 요청입니다.";
            case 401 -> "로그인이 필요합니다.";
            case 403 -> "접근 권한이 없습니다.";
            case 404 -> "요청하신 페이지를 찾을 수 없습니다.";
            case 409 -> "요청을 처리할 수 없습니다.";
            default -> "서버 오류가 발생했습니다.";
        };
    }

    private String getMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "입력값을 확인한 뒤 다시 시도해주세요.";
            case 401 -> "로그인 후 다시 시도해주세요.";
            case 403 -> "현재 계정으로 접근할 수 없는 페이지입니다.";
            case 404 -> "입력한 주소가 올바른지 확인하거나 메인에서 다시 이동해주세요.";
            case 409 -> "요청한 작업이 현재 데이터 상태와 맞지 않습니다.";
            default -> "처리 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
        };
    }

    private String getOriginalPath(HttpServletRequest request) {
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String path = requestUri == null ? request.getRequestURI() : requestUri.toString();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }

        return path;
    }
}
