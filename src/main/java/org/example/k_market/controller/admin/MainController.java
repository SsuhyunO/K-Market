package org.example.k_market.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.service.admin.AdminMainService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final AdminMainService adminMainService;

    @GetMapping({"/admin/main", "/admin"})
    public String main(HttpSession session, Model model) {
        String loginMemberType = (String) session.getAttribute("loginMemberType");
        String memberUid = (String) session.getAttribute("loginMember");

        if (loginMemberType == null) {
            return "redirect:/";
        }

        model.addAttribute("orderStat", adminMainService.getOrderStat(loginMemberType, memberUid));
        model.addAttribute("operationCard", adminMainService.getOperationCard(loginMemberType, memberUid));
        model.addAttribute("memberStat", adminMainService.getMemberStat(loginMemberType));
        model.addAttribute("visitStat", adminMainService.getVisitStat(loginMemberType, memberUid));
        model.addAttribute("inquiryStat", adminMainService.getInquiryStat(loginMemberType));
        model.addAttribute("recentNotices", adminMainService.getRecentNotices(loginMemberType));
        model.addAttribute("recentInquiries", adminMainService.getRecentInquiries(loginMemberType));
        model.addAttribute("categorySalesDaily", adminMainService.getCategorySalesDaily(loginMemberType, memberUid));
        model.addAttribute("categorySalesTotal", adminMainService.getCategorySalesTotal(loginMemberType, memberUid));
        model.addAttribute("loginMemberType", loginMemberType); // 화면에서 메뉴 노출 여부 등에 필요하면 같이 전달


        return "/admin/main";
    }
}
