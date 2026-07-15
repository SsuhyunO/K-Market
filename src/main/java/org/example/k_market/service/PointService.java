package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.PointDAO;
import org.example.k_market.dto.PointDTO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.point.response.PointListResponse;
import org.example.k_market.entity.Point;
import org.example.k_market.repository.PointRepository;
import org.example.k_market.service.pagination.PageQuery;
import org.example.k_market.service.pagination.PaginationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private static final int POINT_EXPIRE_MONTHS = 12;
    private static final int MY_PAGE_POINT_SIZE = 5;
    private static final int POINT_LIST_SIZE = 10;
    private static final int POINT_PAGE_BLOCK_SIZE = 5;

    private final PointDAO pointDAO;
    private final PointRepository pointRepository;
    private final PaginationService paginationService;

    @Transactional
    public void usePoint(String memberUid, int usedPoints, Integer orderNo) {
        if (usedPoints <= 0) return;

        int balance = pointDAO.selectPointBalance(memberUid);
        if (balance < usedPoints) {
            throw new IllegalStateException("보유 포인트가 부족합니다.");
        }

        PointDTO usage = PointDTO.builder()
            .memberUid(memberUid)
            .orderNo(orderNo)
            .point(-usedPoints)
            .content("사용")
            .note("주문 결제 시 포인트 사용")
            .expireDate(null)
            .build();

        pointDAO.insertPoint(usage);
    }

    @Transactional
    public void earnPoint(String memberUid, int earnedPoints, Integer orderNo) {
        if (earnedPoints <= 0) return;

        PointDTO earn = PointDTO.builder()
            .memberUid(memberUid)
            .orderNo(orderNo)
            .point(earnedPoints)
            .content("적립")
            .note("주문 완료 적립")
            .expireDate(LocalDateTime.now().plusMonths(POINT_EXPIRE_MONTHS))
            .build();

        pointDAO.insertPoint(earn);
    }

    public int getBalance(String memberUid) {
        return pointDAO.selectPointBalance(memberUid);
    }

    public List<PointListResponse> getRecentPointsByMemberUid(String memberUid) {
        Pageable pageable = PageRequest.of(0, MY_PAGE_POINT_SIZE, Sort.by("createdAt").descending());

        return pointRepository.findByMemberUid(memberUid, pageable)
            .stream()
            .map(this::toPointListResponse)
            .toList();
    }

    public PageResponse<PointListResponse> getPointPageByMemberUid(
        String memberUid,
        int page,
        LocalDate startDate,
        LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return paginationService.getPageInfo(
            page,
            POINT_LIST_SIZE,
            POINT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<PointListResponse> fetch(int offset, int size) {
                    Pageable pageable = PageRequest.of(
                        offset / size,
                        size,
                        Sort.by("createdAt").descending()
                    );

                    return findPoints(memberUid, startDateTime, endDateTime, pageable)
                        .stream()
                        .map(PointService.this::toPointListResponse)
                        .toList();
                }

                @Override
                public int count() {
                    if (hasDateRange(startDateTime, endDateTime)) {
                        return pointRepository.countByMemberUidAndCreatedAtBetween(
                            memberUid,
                            startDateTime,
                            endDateTime
                        );
                    }

                    return pointRepository.countByMemberUid(memberUid);
                }
            }
        );
    }

    private List<Point> findPoints(
        String memberUid,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Pageable pageable
    ) {
        if (hasDateRange(startDateTime, endDateTime)) {
            return pointRepository.findByMemberUidAndCreatedAtBetween(
                memberUid,
                startDateTime,
                endDateTime,
                pageable
            );
        }

        return pointRepository.findByMemberUid(memberUid, pageable);
    }

    private boolean hasDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime != null && endDateTime != null;
    }

    private PointListResponse toPointListResponse(Point point) {
        return PointListResponse.builder()
            .pointNo(point.getPointNo())
            .createdAt(point.getCreatedAt().toLocalDate().toString())
            .content(point.getContent())
            .orderNo(point.getOrderNo())
            .point(point.getPoint())
            .note(point.getNote())
            .expireDate(point.getExpireDate())
            .build();
    }

    // =================================================================
    // 관리자 페이지용: 포인트 내역 조회 및 페이징
    // =================================================================

    /**
     * 조건에 맞는 전체 포인트 내역 건수 조회
     */
    public int getTotalCount(String searchType, String keyword) {
        if (searchType != null && searchType.isBlank()) searchType = null;
        if (keyword != null && keyword.isBlank()) keyword = null;

        return pointDAO.selectPointCount(searchType, keyword);
    }

    /**
     * 조건에 맞는 페이징된 포인트 내역 조회
     */
    public List<PointDTO> getPointList(String searchType, String keyword, int page, int pageSize) {
        if (searchType != null && searchType.isBlank()) searchType = null;
        if (keyword != null && keyword.isBlank()) keyword = null;

        // DB에서 가져올 시작 위치(offset) 계산
        int offset = (page - 1) * pageSize;

        return pointDAO.selectPointList(searchType, keyword, offset, pageSize);
    }

    @Transactional
    public void deletePoints(List<Integer> pointNos) {
        pointDAO.deletePoints(pointNos);
    }

    public List<PointDTO> getRecentEarnedPoints(String memberUid, int limit) {
        return pointDAO.selectRecentEarnedPoints(memberUid, limit);
    }
}
