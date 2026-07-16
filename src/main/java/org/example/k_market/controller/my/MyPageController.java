package org.example.k_market.controller.my;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PointDTO;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.dto.admin.QnaDTO;
import org.example.k_market.service.PointService;
import org.example.k_market.service.admin.BannerService;
import org.example.k_market.service.admin.CouponIssueService;
import org.example.k_market.service.admin.QnaService;
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
    private final QnaService qnaService;

    /**
     * 마이페이지 공통 배너
     */
    @ModelAttribute
    public void addMyPageBanner(Model model) {
        BannerDTO myPageBanner =
                bannerService.findFirstEnabledBannerByType("myPage");

        model.addAttribute("myPageBanner", myPageBanner);
    }

    /* 마이페이지 공통 요약 정보 */
    @ModelAttribute
    public void addMyPageSummary(
            HttpSession session,
            Model model
    ) {
        String memberUid =
                (String) session.getAttribute("loginMember");

        int totalOrderCount = 0;
        int myCouponCount = 0;
        int myPointBalance = 0;
        List<QnaDTO> qnaList = null;

        if (memberUid != null && !memberUid.isBlank()) {
            totalOrderCount = orderService.getTotalCount("memberUid", memberUid);
            myCouponCount = couponIssueService.getMyAvailableCouponCount(memberUid);
            myPointBalance = pointService.getBalance(memberUid);
            qnaList = qnaService.getQnaListByMemberUid(memberUid.trim());
        }

        model.addAttribute("qnaList", qnaList);
        model.addAttribute("totalOrderCount", totalOrderCount);
        model.addAttribute("myCouponCount", myCouponCount);
        model.addAttribute("myPointBalance", myPointBalance);
    }

    /**
     * 마이페이지 홈
     */
    @GetMapping("/my/home")
    public String home(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");

        if (memberUid == null ||
                memberUid.isBlank()) {

            return "redirect:/member/login";
        }

        String safeMemberUid =
                memberUid.trim();

        model.addAttribute("recentPoints", pointService.getRecentPointsByMemberUid(safeMemberUid));
        model.addAttribute("recentReviews", reviewService.getRecentReviewsByMemberId(safeMemberUid));

        List<PointDTO> pointList = pointService.getRecentEarnedPoints(safeMemberUid, 5);
        model.addAttribute("pointList", pointList);

        /*
         * 마이페이지 홈 하단 최근 문의 5개
         */
        model.addAttribute("recentQnaList", qnaService.getRecentQnaListByMemberUid(safeMemberUid));

        return "my/home";
    }

    /**
     * 전체 주문내역
     */
    @GetMapping("/my/order")
    public String order(
            HttpSession session
    ) {
        if (!isLoggedIn(session)) {
            return "redirect:/member/login";
        }

        return "my/order";
    }

    /**
     * 쿠폰
     */
    @GetMapping("/my/coupon")
    public String coupon(
            HttpSession session
    ) {
        if (!isLoggedIn(session)) {
            return "redirect:/member/login";
        }

        return "my/coupon";
    }

    /**
     * 로그인 회원의 전체 문의내역
     */
    @GetMapping("/my/qna")
    public String qna(HttpSession session, Model model) {
        String memberUid = (String) session.getAttribute("loginMember");

        if (memberUid == null ||
                memberUid.isBlank()) {

            return "redirect:/member/login";
        }

        List<QnaDTO> qnaList = qnaService.getQnaListByMemberUid(memberUid.trim());
        model.addAttribute("qnaList", qnaList);

        return "my/qna";
    }

    /**
     * 나의 설정
     */
    @GetMapping("/my/info")
    public String info(
            HttpSession session
    ) {
        if (!isLoggedIn(session)) {
            return "redirect:/member/login";
        }

        return "my/info";
    }

    /* 리뷰 목록 */
    @GetMapping("/review/list")
    public String reviewList(
            HttpSession session
    ) {
        if (!isLoggedIn(session)) {
            return "redirect:/member/login";
        }

        return "my/review";
    }

    /* 로그인 여부 확인 */
    private boolean isLoggedIn(
            HttpSession session
    ) {
        String memberUid =
                (String) session.getAttribute("loginMember");

        return memberUid != null &&
                !memberUid.isBlank();
    }
    
    @GetMapping("/point/list")
    public String point() {
        return "my/point";
    }
}