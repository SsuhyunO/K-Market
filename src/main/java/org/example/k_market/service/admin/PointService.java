package org.example.k_market.service.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.PointDAO;
import org.example.k_market.dto.PointDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointDAO pointDAO;

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
}
