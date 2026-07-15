package org.example.k_market.controller.review;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.ReviewDTO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.review.response.ReviewListResponse;
import org.example.k_market.service.review.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping("/write")
    public ResponseEntity<Void> writeReview(
            @ModelAttribute ReviewDTO reviewDTO,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session
    ) {
        String memberUid = getLoginMemberUid(session);
        reviewDTO.setMemberUid(memberUid);

        reviewService.writeReview(reviewDTO, photo);

        return ResponseEntity.ok().build();
    }
}
