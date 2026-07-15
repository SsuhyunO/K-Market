package org.example.k_market.controller.admin.member;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PointDTO;
import org.example.k_market.service.PointService;
import org.example.k_market.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller("adminPointController")
@RequestMapping("/admin")
public class PointController {
    private final PointService pointService;

    @GetMapping("/management-point")
    public String point(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(required = false) String searchType,
                        @RequestParam(required = false) String keyword,
                        Model model) {

        // 1. 전체 포인트 내역 건수 조회
        int totalCount = pointService.getTotalCount(searchType, keyword);

        // 2. 페이징 객체 생성
        PageInfo pageInfo = new PageInfo(page, totalCount);

        // 3. 페이지 정보에 맞게 포인트 내역 조회
        List<PointDTO> pointList = pointService.getPointList(searchType, keyword, page, pageInfo.getPageSize());

        // 4. 화면(View)으로 데이터 전달
        model.addAttribute("pointList", pointList);
        model.addAttribute("pageInfo", pageInfo);

        // 5. 검색 조건 유지를 위해 Model에 다시 담기
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "admin/member/point";
    }

    @PostMapping("/management-point/delete")
    public String deletePoints(@RequestParam(name = "pointNo", required = false) List<Integer> pointNos) {

        // 체크박스가 선택된 상태로 넘어왔을 때만 삭제 로직 실행
        if (pointNos != null && !pointNos.isEmpty()) {
            pointService.deletePoints(pointNos);
        }

        // 처리가 끝나면 다시 포인트 관리 목록 페이지로 리다이렉트 (새로고침 효과)
        return "redirect:/admin/management-point";
    }
}
