package org.example.k_market.controller.member;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.service.PolicyService;
import org.example.k_market.service.admin.BannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final PolicyService policyService;
    private final BannerService bannerService;
    // TODO: 실제 로그인 검증용 서비스로 교체하세요 (예: private final MemberService memberService;)

    @GetMapping("/member/login")
    public String login(Model model) {

        BannerDTO loginBanner =
                bannerService.findFirstEnabledBannerByType("userLogin");

        model.addAttribute("loginBanner", loginBanner);

        return "member/login";
    }

    @PostMapping("/member/login")
    public String loginProc(@RequestParam String userId,
                            @RequestParam String userPw,
                            HttpSession session,
                            Model model) {

        // TODO: memberService.login(userId, userPw) 등 실제 검증 로직으로 교체
        // 검증 실패 시:
        // model.addAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
        // return "member/login";

        // 검증 성공 시 (uid는 실제 조회한 회원 uid로 교체):
        session.setAttribute("loginMember", userId);

        return "redirect:/";
    }

    @GetMapping("/member/join")
    public String join() {
        return "member/join";
    }

    @GetMapping("/member/register")
    public String register() {
        return "member/register";
    }

    @GetMapping("/member/registerSeller")
    public String registerSeller() {
        return "member/registerSeller";
    }

    @GetMapping("/member/password")
    public String password() {
        return "member/password";
    }

    @GetMapping("/member/changepassword")
    public String changepassword() {
        return "member/changepassword";
    }

    @GetMapping("/member/signup")
    public String singup(Model model) {

        model.addAttribute(
                "buyerPolicy",
                policyService.getPolicyContent("buyer")
        );

        model.addAttribute(
                "sellerPolicy",
                policyService.getPolicyContent("seller")
        );

        model.addAttribute(
                "financePolicy",
                policyService.getPolicyContent("finance")
        );

        model.addAttribute(
                "locationPolicy",
                policyService.getPolicyContent("location")
        );

        model.addAttribute(
                "privacyPolicy",
                policyService.getPolicyContent("privacy")
        );

        return "member/signup";
    }

    @GetMapping("/member/userid")
    public String userid() {
        return "member/userid";
    }

    @GetMapping("/member/resultid")
    public String resultid() {
        return "member/resultid";
    }
}