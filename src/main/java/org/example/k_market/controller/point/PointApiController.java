package org.example.k_market.controller.point;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.point.response.PointListResponse;
import org.example.k_market.service.point.PointService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointApiController {
    private final PointService pointService;

    @GetMapping("/my/list")
    public ResponseEntity<PageResponse<PointListResponse>> getMyPoints(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        HttpSession session
    ) {
        String memberUid = getLoginMemberUid(session);

        return ResponseEntity.ok(pointService.getPointPageByMemberUid(
            memberUid,
            page,
            startDate,
            endDate
        ));
    }

    private String getLoginMemberUid(HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberUid;
    }
}
