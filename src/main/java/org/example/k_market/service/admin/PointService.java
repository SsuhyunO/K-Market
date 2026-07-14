package org.example.k_market.service.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.PointDAO;
import org.example.k_market.dto.PointDTO;
import org.example.k_market.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointDAO pointDAO;
    private final PointRepository pointRepository;

    private static final int POINT_EXPIRE_MONTHS = 12; // 적립 포인트 유효기간 예시

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
                .point(-usedPoints)      // 사용은 음수
                .content("사용")
                .note("주문 결제 시 포인트 사용")
                .expireDate(null)        // 사용 건은 만료일 없음
                .build();

        pointDAO.insertPoint(usage);
    }

    @Transactional
    public void earnPoint(String memberUid, int earnedPoints, Integer orderNo) {
        if (earnedPoints <= 0) return;

        PointDTO earn = PointDTO.builder()
                .memberUid(memberUid)
                .orderNo(orderNo)
                .point(earnedPoints)     // 적립은 양수
                .content("적립")
                .note("주문 완료 적립")
                .expireDate(LocalDateTime.now().plusMonths(POINT_EXPIRE_MONTHS))
                .build();

        pointDAO.insertPoint(earn);
    }

    public int getBalance(String memberUid) {
        return pointDAO.selectPointBalance(memberUid);
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
