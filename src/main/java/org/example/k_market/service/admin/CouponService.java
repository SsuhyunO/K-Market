package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.CouponDAO;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.repository.coupon.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponDAO couponDAO;

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

    public int getTotalCount() {
        return couponDAO.getTotalCount();
    }

    public List<CouponDTO> getCouponList(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return couponDAO.getCouponList(offset, pageSize);
    }

    public void endCoupon(int couponNo) {
        couponDAO.updateStatusToDisabled(couponNo);
    }
}
