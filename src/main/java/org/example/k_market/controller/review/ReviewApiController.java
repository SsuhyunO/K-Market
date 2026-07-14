package org.example.k_market.controller.review;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.review.response.ReviewListResponse;
import org.example.k_market.service.review.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/review/api")
@RequiredArgsConstructor
public class ReviewApiController {
    private final ReviewService reviewService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<ReviewListResponse>> getListByMyPage(
        @RequestParam(defaultValue = "1") int page,
        HttpSession session
    ) {
        String memberUid = getLoginMemberUid(session);

        return ResponseEntity.ok(reviewService.getReviewPageByMemberId(memberUid, page));
    }

    @GetMapping("/product/{prodNo}/list")
    public ResponseEntity<PageResponse<ReviewListResponse>> getListByProduct(
        @PathVariable int prodNo,
        @RequestParam(defaultValue = "1") int page
    ) {
        return ResponseEntity.ok(reviewService.getReviewPageByProductNo(prodNo, page));
    }

    private String getLoginMemberUid(HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberUid;
    }
}
