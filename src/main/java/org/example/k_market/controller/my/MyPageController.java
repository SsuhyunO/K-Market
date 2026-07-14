package org.example.k_market.controller.my;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.service.review.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {
    private final ReviewService reviewService;

    @GetMapping("/my/home")
    public String home(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid != null && !memberUid.isBlank()) {
            model.addAttribute("recentReviews", reviewService.getRecentReviewsByMemberId(memberUid));
        }

        return "my/home";
    }

    @GetMapping("/my/order")
    public String order() {
        return "my/order";
    }

    @GetMapping("/my/coupon")
    public String coupon() {
        return "my/coupon";
    }

    @GetMapping("/my/qna")
    public String qna() {
        return "my/qna";
    }

    @GetMapping("/my/info")
    public String info() {
        return "my/info";
    }

}
