package org.example.k_market.service.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.CouponDAO;
import org.example.k_market.dao.CouponIssueDAO;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.repository.coupon.CouponRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponDAO couponDAO;
    private final CouponIssueDAO couponIssueDAO;

    public List<CouponDTO> findAll() {
        return couponDAO.findAll();
    }

    public void register(CouponDTO dto){
        // expireDate가 없으면 validDays로 계산해서 넣어줌
        if (dto.getExpireDate() == null || dto.getExpireDate().isBlank()) {
            LocalDate baseDate = (dto.getStartDate() == null || dto.getStartDate().isBlank())
                    ? LocalDate.now()
                    : LocalDate.parse(dto.getStartDate());

            dto.setExpireDate(baseDate.plusDays(dto.getValidDays()).toString());
        }

        couponDAO.insert(dto);
    }

    public List<CouponDTO> getCouponList(String searchType, String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return couponDAO.getCouponList(searchType, keyword, offset, pageSize);
    }

    public int getTotalCount(String searchType, String keyword) {
        return couponDAO.getTotalCount(searchType, keyword);
    }

    @Transactional
    public void endCoupon(int couponNo) {
        couponDAO.updateStatusToDisabled(couponNo);
        couponIssueDAO.stopIssuesByCouponNo(couponNo);
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    @Transactional
    public void disableExpiredCoupons() {
        couponDAO.disableExpiredCoupons();
        couponIssueDAO.expireIssuesByExpiredCoupons();
    }

    public CouponDTO getCouponByNo(int couponNo) {
        return couponDAO.getCouponByNo(couponNo);
    }
}
