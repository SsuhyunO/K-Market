package org.example.k_market.advice.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProductMultipartExceptionHandler {

    private static final String PRODUCT_REGISTER_PATH = "/admin/product/register";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              RedirectAttributes redirectAttributes) throws IOException {
        if (!isProductRegisterRequest(request)) {
            response.sendError(HttpStatus.PAYLOAD_TOO_LARGE.value());
            return null;
        }

        redirectAttributes.addFlashAttribute("productErrorMessage", resolveMessage(exception));
        return "redirect:/admin/product/register";
    }

    private String resolveMessage(MaxUploadSizeExceededException exception) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);
        if (rootCause.getClass().getSimpleName().equals("FileCountLimitExceededException")) {
            return "상품 옵션 또는 입력 항목이 너무 많습니다. 항목 수를 줄인 뒤 다시 시도해주세요.";
        }

        return "업로드 가능한 파일 크기를 초과했습니다.";
    }

    private boolean isProductRegisterRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }

        return PRODUCT_REGISTER_PATH.equals(requestUri);
    }
}
