package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.CouponIssueDAO;
import org.example.k_market.dao.CouponDAO;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.dto.coupon.CouponIssueDTO;
import org.example.k_market.dto.order.OrderItemViewDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponIssueService {
    private final CouponIssueDAO couponIssueDAO;
    private final CouponDAO couponDAO;

    private static final int STATUS_READY = 0;    // 미사용
    private static final int STATUS_USED = 1;     // 사용완료
    private static final int STATUS_EXPIRED = 2;  // 기간만료
    private static final int STATUS_STOPPED = 3;  // 중단

    public int getTotalCount(String searchType, String keyword, String sellerUidScope) {
        return couponIssueDAO.getTotalCount(searchType, keyword, sellerUidScope);
    }

    public List<CouponIssueDTO> getCouponIssueList(String searchType, String keyword, String sellerUidScope,
                                                   int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return couponIssueDAO.getCouponIssueList(searchType, keyword, sellerUidScope, offset, pageSize);
    }

    public CouponIssueDTO getCouponIssueByNo(int issueNo) {
        return couponIssueDAO.getCouponIssueByNo(issueNo);
    }

    public void stopCouponIssue(int issueNo, String sellerUidScope) {
        CouponIssueDTO issue = couponIssueDAO.getCouponIssueByNo(issueNo);

        if (issue == null) {
            throw new IllegalArgumentException("존재하지 않는 발급 쿠폰입니다.");
        }

        if (issue.getStatus() != STATUS_READY) {
            throw new IllegalStateException("미사용 상태의 쿠폰만 중단할 수 있습니다.");
        }
        if (sellerUidScope != null && !sellerUidScope.equals(issue.getSellerUid())) {
            throw new SecurityException("본인이 발급한 쿠폰만 중단할 수 있습니다.");
        }

        couponIssueDAO.stopCouponIssue(issueNo, STATUS_STOPPED);
    }

    public List<CouponIssueDTO> getAvailableCoupons(String memberUid, List<OrderItemViewDTO> orderItems) {
        List<String> sellerUidList = orderItems.stream()
                .map(OrderItemViewDTO::getSellerUid)
                .distinct()
                .collect(Collectors.toList());

        return couponIssueDAO.getAvailableCouponsByMemberUid(memberUid, sellerUidList);
    }

    /**
     * 결제 완료 시 쿠폰을 사용완료 처리한다.
     * 상태(READY) 및 만료일을 재검증한 뒤 USED로 변경한다.
     */
    @Transactional
    public void markAsUsed(int issueNo, String memberUid) {
        CouponIssueDTO issue = couponIssueDAO.getCouponIssueByNo(issueNo);

        if (issue == null) {
            throw new IllegalArgumentException("존재하지 않는 발급 쿠폰입니다.");
        }
        if (!memberUid.equals(issue.getMemberUid())) {
            throw new IllegalStateException("본인의 쿠폰이 아닙니다.");
        }
        if (issue.getStatus() != STATUS_READY) {
            throw new IllegalStateException("이미 사용되었거나 사용할 수 없는 쿠폰입니다.");
        }
        if (issue.getExpireDate() != null
                && java.time.LocalDate.parse(issue.getExpireDate().substring(0, 10))
                .isBefore(java.time.LocalDate.now())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        int updated = couponIssueDAO.markAsUsed(issueNo, STATUS_USED);
        if (updated == 0) {
            throw new IllegalStateException("이미 사용되었거나 사용할 수 없는 쿠폰입니다.");
        }
        couponDAO.incrementUsedCount(issue.getCouponNo());
    }

    public int getMyAvailableCouponCount(String memberUid) {
        return couponIssueDAO.getMyAvailableCouponCount(memberUid);
    }

    /**
     * 일반회원 가입 완료 시 현재 사용 가능한 관리자 쿠폰을 지급한다.
     * 관리자 쿠폰은 sellerUid가 NULL인 쿠폰으로 구분한다.
     */
    @Transactional
    public List<String> issueSignupCoupons(String memberUid) {
        if (memberUid == null || memberUid.isBlank()) {
            throw new IllegalArgumentException("쿠폰을 지급할 회원 정보가 없습니다.");
        }

        List<String> issuedCouponNames = new java.util.ArrayList<>();
        for (CouponDTO coupon : couponDAO.getActiveAdminCoupons()) {
            int issued = couponIssueDAO.issueCouponIfEligible(
                    coupon.getCouponNo(),
                    memberUid.trim()
            );
            if (issued > 0) {
                issuedCouponNames.add(coupon.getName());
            }
        }
        return issuedCouponNames;
    }

    /**
     * 해당 판매자의 상품을 처음 구매확정한 회원에게 판매자 쿠폰을 지급한다.
     */
    @Transactional
    public List<String> issueFirstPurchaseCoupons(String sellerUid, String memberUid) {
        if (sellerUid == null || sellerUid.isBlank()
                || memberUid == null || memberUid.isBlank()) {
            return List.of();
        }

        List<String> issuedCouponNames = new java.util.ArrayList<>();
        for (CouponDTO coupon : couponDAO.getActiveSellerCoupons(sellerUid.trim())) {
            int issued = couponIssueDAO.issueCouponIfEligible(
                    coupon.getCouponNo(),
                    memberUid.trim()
            );
            if (issued > 0) {
                String issuerName = coupon.getCompanyName() == null
                        || coupon.getCompanyName().isBlank()
                        ? coupon.getSellerUid()
                        : coupon.getCompanyName();
                issuedCouponNames.add(issuerName + " - " + coupon.getName());
            }
        }
        return issuedCouponNames;
    }

    public List<CouponIssueDTO> getMyCoupons(String memberUid) {
        if (memberUid == null || memberUid.isBlank()) {
            return List.of();
        }
        return couponIssueDAO.getMyCouponList(memberUid.trim());
    }
}
