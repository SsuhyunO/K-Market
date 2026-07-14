package org.example.k_market.service.point;

import lombok.RequiredArgsConstructor;
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

@Service("myPointService")
@RequiredArgsConstructor
public class PointService {
    private static final int POINT_LIST_SIZE = 10;
    private static final int POINT_PAGE_BLOCK_SIZE = 5;

    private final PointRepository pointRepository;
    private final PaginationService paginationService;

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
}
