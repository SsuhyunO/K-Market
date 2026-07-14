package org.example.k_market.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.SalesDAO;
import org.example.k_market.dto.shop.SalesSearchRequest;
import org.example.k_market.dto.shop.SalesStatusDTO;
import org.example.k_market.dto.shop.SalesStatusResult;
import org.example.k_market.util.PageInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesDAO salesDAO;

    @Override
    public SalesStatusResult getSalesStatus(SalesSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("period", request.getPeriod());
        params.put("size", request.getSize());
        params.put("offset", request.getOffset());

        List<SalesStatusDTO> rows = salesDAO.selectSalesStatusList(params);

        // 화면 표시용 번호 부여 (역순: 목록 위쪽이 큰 번호)
        int total = salesDAO.countSalesStatus(params);
        int startNo = total - request.getOffset();
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setNo(startNo - i);
        }

        PageInfo pageInfo = new PageInfo(request.getPage(), total, request.getSize(), 5);

        return new SalesStatusResult(rows, pageInfo);
    }
}