package org.example.k_market.controller.admin.order;

import lombok.RequiredArgsConstructor;
import org.example.k_market.util.PageInfo;
import org.example.k_market.dto.order.OrderDTO;
import org.example.k_market.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/order-list")
    public String orderList(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String searchType,
                            @RequestParam(required = false) String keyword,
                            Model model) {
        // 1. 전체 주문 수 조회
        int totalCount = orderService.getTotalCount(searchType, keyword);

        // 2. PageInfo 객체 생성 (이 코드가 있어야 뷰에서 null 에러가 안 납니다!)
        PageInfo pageInfo = new PageInfo(page, totalCount);

        // 3. 페이지 정보에 맞게 주문 목록 조회
        List<OrderDTO> orderList = orderService.getOrderList(searchType, keyword, page, pageInfo.getPageSize());

        // 4. Model에 데이터 담기 (가장 중요)
        model.addAttribute("orderList", orderList);
        model.addAttribute("pageInfo", pageInfo); // 뷰로 넘겨줌!

        // 검색 조건 유지를 위해 Model에 다시 담기
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "admin/order/order-list";
    }

    @GetMapping("/delivery-list")
    public String deliveryList() {
        return "admin/order/delivery-list";
    }
}
