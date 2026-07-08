package org.example.k_market.advice.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = org.example.k_market.controller.admin.product.ProductController.class)
@Slf4j
public class ProductExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleRegisterValidationException(RuntimeException exception,
                                                    HttpServletRequest request,
                                                    RedirectAttributes redirectAttributes) {
        if (isProductDeleteRequest(request)) {
            redirectAttributes.addFlashAttribute("productListErrorMessage", exception.getMessage());
            return "redirect:/admin/product/list";
        }

        redirectAttributes.addFlashAttribute("productErrorMessage", exception.getMessage());
        return "redirect:/admin/product/register";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRegisterException(RuntimeException exception,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {
        if (isProductDeleteRequest(request)) {
            log.error("상품 삭제 실패", exception);
            redirectAttributes.addFlashAttribute(
                "productListErrorMessage",
                "상품 삭제 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
            return "redirect:/admin/product/list";
        }

        log.error("상품 등록 실패", exception);
        redirectAttributes.addFlashAttribute(
            "productErrorMessage",
            "상품 등록 중 문제가 발생했습니다. 입력값을 확인한 뒤 다시 시도해주세요."
        );
        return "redirect:/admin/product/register";
    }

    private boolean isProductDeleteRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }

        return "/admin/product/remove".equals(requestUri);
    }
}
