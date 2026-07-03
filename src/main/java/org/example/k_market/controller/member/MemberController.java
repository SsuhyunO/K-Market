package org.example.k_market.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

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
    public String singup() {
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