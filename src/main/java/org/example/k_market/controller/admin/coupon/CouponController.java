package org.example.k_market.controller.admin.coupon;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.coupon.CouponDTO;
import org.example.k_market.dto.coupon.CouponIssueDTO;
import org.example.k_market.entity.Seller;
import org.example.k_market.service.seller.SellerService;
import org.example.k_market.service.admin.CouponIssueService;
import org.example.k_market.service.admin.CouponService;
import org.example.k_market.util.PageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController {
    private final CouponService couponService;
    private final SellerService sellerService;
    private final CouponIssueService couponIssueService;

    @GetMapping({"/list", "", "/"})
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(name = "search-type", required = false) String searchType,
                       @RequestParam(required = false) String keyword,
                       HttpSession session,
                       Model model) {
        String loginUid = (String) session.getAttribute("loginMember");
        String memberType = (String) session.getAttribute("loginMemberType");

        if ("SELLER".equals(memberType)) {
            Seller seller = sellerService.findByUid(loginUid);
            model.addAttribute("companyName", seller.getCompanyName());
        } else {
            // ADMIN이거나 그 외의 경우
            model.addAttribute("companyName", "최고관리자");
        }

        int totalCount = couponService.getTotalCount(searchType, keyword);
        PageInfo pageInfo = new PageInfo(page, totalCount); // 기존 방식대로 생성자에서 totalCount 넘김

        List<CouponDTO> couponList = couponService.getCouponList(searchType, keyword, page, pageInfo.getPageSize());
        model.addAttribute("couponList", couponList);
        model.addAttribute("pageInfo", pageInfo);

        // 검색폼에 값 유지시키기 위해 다시 넘겨줌
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        model.addAttribute("memberType", memberType);
        model.addAttribute("loginUid", loginUid);

        return "admin/coupon/list";
    }

    @PostMapping("/register")
    public String register(CouponDTO dto, HttpSession session){
        String loginUid = (String) session.getAttribute("loginMember");
        dto.setSellerUid(loginUid); // 폼값 무시하고 세션값으로 덮어쓰기

        couponService.register(dto);

        return "redirect:/admin/coupon";
    }

    @PatchMapping("/{couponNo}/end")
    @ResponseBody
    public ResponseEntity<Void> endCoupon(@PathVariable int couponNo, HttpSession session) {
        String loginUid = (String) session.getAttribute("loginMember");
        String memberType = (String) session.getAttribute("loginMemberType");

        CouponDTO coupon = couponService.getCouponByNo(couponNo);
        if (coupon == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = "ADMIN".equals(memberType);
        boolean isOwner = "SELLER".equals(memberType)
                && loginUid != null
                && loginUid.equals(coupon.getSellerUid());

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        couponService.endCoupon(couponNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/used")
    public String used(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(name = "search-type", required = false) String searchType,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        int totalCount = couponIssueService.getTotalCount(searchType, keyword);
        PageInfo pageInfo = new PageInfo(page, totalCount);

        List<CouponIssueDTO> issueList = couponIssueService.getCouponIssueList(searchType, keyword, page, pageInfo.getPageSize());
        model.addAttribute("issueList", issueList);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "admin/coupon/used";
    }

    @PatchMapping("/issue/{issueNo}/stop")
    @ResponseBody
    public ResponseEntity<Void> stopIssue(@PathVariable int issueNo) {
        try {
            couponIssueService.stopCouponIssue(issueNo);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
    }
}
