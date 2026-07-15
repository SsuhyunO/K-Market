package org.example.k_market.controller.my;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PointDTO;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.service.PointService;
import org.example.k_market.service.admin.BannerService;
import jakarta.servlet.http.HttpSession;
import org.example.k_market.service.admin.CouponIssueService;
import org.example.k_market.service.order.OrderService;
import org.example.k_market.service.review.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {
    private final ReviewService reviewService;
    private final PointService pointService;
    private final OrderService orderService;
    private final BannerService bannerService;
    private final CouponIssueService couponIssueService;

    @ModelAttribute
    public void addMyPageBanner(Model model) {

        BannerDTO myPageBanner =
                bannerService.findFirstEnabledBannerByType("myPage");

        model.addAttribute("myPageBanner", myPageBanner);
    }

    @ModelAttribute
    public void addMyPageSummary(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");

        int totalOrderCount = 0;
        if (memberUid != null && !memberUid.isBlank()) {
            totalOrderCount = orderService.getTotalCount("memberUid", memberUid);
        }

        int myCouponCount = 0;
        if (memberUid != null && !memberUid.isBlank()) {
            myCouponCount = couponIssueService.getMyAvailableCouponCount(memberUid);
        }

        int myPointBalance = 0;
        if (memberUid != null && !memberUid.isBlank()) {
            myPointBalance = pointService.getBalance(memberUid);
        }

        model.addAttribute("totalOrderCount", totalOrderCount);
        model.addAttribute("myCouponCount", myCouponCount);
        model.addAttribute("myPointBalance", myPointBalance);
    }

    @GetMapping("/my/home")
    public String home(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid != null && !memberUid.isBlank()) {
            model.addAttribute("recentPoints", pointService.getRecentPointsByMemberUid(memberUid));
            model.addAttribute("recentReviews", reviewService.getRecentReviewsByMemberId(memberUid));

            // 최근 포인트 적립 내역 5건 조회
            List<PointDTO> pointList = pointService.getRecentEarnedPoints(memberUid, 5);

            model.addAttribute("pointList", pointList);
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

    @GetMapping("/review/list")
    public String list() {
        return "my/review";
    }

}
