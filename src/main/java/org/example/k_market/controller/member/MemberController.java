package org.example.k_market.controller.member;

import lombok.RequiredArgsConstructor;
import org.example.k_market.service.PolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MemberController {
    private final PolicyService policyService;

    @GetMapping("/member/login")
    public String login() {
        return "member/login";
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
        model.addAttribute("buyerPolicy", policyService.getPolicyContent("buyer"));
        model.addAttribute("sellerPolicy", policyService.getPolicyContent("seller"));
        model.addAttribute("financePolicy", policyService.getPolicyContent("finance"));
        model.addAttribute("locationPolicy", policyService.getPolicyContent("location"));
        model.addAttribute("privacyPolicy", policyService.getPolicyContent("privacy"));

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