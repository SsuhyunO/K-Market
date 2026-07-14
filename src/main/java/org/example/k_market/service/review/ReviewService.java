package org.example.k_market.service.review;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.review.response.ReviewListResponse;
import org.example.k_market.entity.Review;
import org.example.k_market.repository.ReviewRepository;
import org.example.k_market.service.pagination.PageQuery;
import org.example.k_market.service.pagination.PaginationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final int MY_PAGE_REVIEW_SIZE = 5;
    private static final int REVIEW_LIST_SIZE = 10;
    private static final int PRODUCT_REVIEW_LIST_SIZE = 5;
    private static final int REVIEW_PAGE_BLOCK_SIZE = 5;

    private final ReviewRepository reviewRepository;
    private final PaginationService paginationService;

    public List<ReviewListResponse> getRecentReviewsByMemberId(String memberUid) {
        Pageable pageable = PageRequest.of(0, MY_PAGE_REVIEW_SIZE, Sort.by("createdAt").descending());

        return reviewRepository
            .findByMemberUid(memberUid, pageable)
            .stream()
            .map(this::toReviewListResponse)
            .toList();
    }

    public PageResponse<ReviewListResponse> getReviewPageByMemberId(String memberUid, int page) {
        return paginationService.getPageInfo(
            page,
            REVIEW_LIST_SIZE,
            REVIEW_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<ReviewListResponse> fetch(int offset, int size) {
                    Pageable pageable = PageRequest.of(
                        offset / size,
                        size,
                        Sort.by("createdAt").descending()
                    );

                    return reviewRepository
                        .findByMemberUid(memberUid, pageable)
                        .stream()
                        .map(ReviewService.this::toReviewListResponse)
                        .toList();
                }

                @Override
                public int count() {
                    return reviewRepository.countByMemberUid(memberUid);
                }
            }
        );
    }

    public PageResponse<ReviewListResponse> getReviewPageByProductNo(int productNo, int page) {
        return paginationService.getPageInfo(
            page,
            PRODUCT_REVIEW_LIST_SIZE,
            REVIEW_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<ReviewListResponse> fetch(int offset, int size) {
                    Pageable pageable = PageRequest.of(
                        offset / size,
                        size,
                        Sort.by("createdAt").descending()
                    );

                    return reviewRepository
                        .findByProductProdNo(productNo, pageable)
                        .stream()
                        .map(ReviewService.this::toReviewListResponse)
                        .toList();
                }

                @Override
                public int count() {
                    return reviewRepository.countByProductProdNo(productNo);
                }
            }
        );
    }

    private ReviewListResponse toReviewListResponse(Review review) {
        return ReviewListResponse.builder()
            .reviewNo(review.getReviewNo())
            .productNo(review.getProduct().getProdNo())
            .productName(review.getProduct().getProdName())
            .memberUid(maskMemberUid(review.getMember() != null ? review.getMember().getUid() : null))
            .content(review.getContent())
            .rating(review.getRating())
            .createdAt(review.getCreatedAt().toLocalDate().toString())
            .build();
    }

    private String maskMemberUid(String memberUid) {
        if (memberUid == null || memberUid.isBlank()) {
            return "";
        }
        if (memberUid.length() <= 2) {
            return memberUid.charAt(0) + "*";
        }
        return memberUid.substring(0, 2) + "*".repeat(memberUid.length() - 2);
    }
}
