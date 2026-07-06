package org.example.k_market.controller.member;

import jakarta.servlet.http.HttpSession;
import org.example.k_market.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/member")
public class EmailAuthController {

    private static final long CODE_VALID_MINUTES = 5; // 인증번호 유효시간(분)

    private final EmailService emailService;

    public EmailAuthController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<String> sendEmailCode(@RequestParam String email, HttpSession session) {
        try {
            String code = String.format("%06d", new Random().nextInt(1000000));

            session.setAttribute("emailAuthCode", code);
            session.setAttribute("emailAuthTarget", email);
            session.setAttribute("emailAuthTime", LocalDateTime.now());
            // 새 코드를 보낼 때는 이전 인증 완료 여부 초기화
            session.setAttribute("emailAuthVerified", false);

            System.out.println("✅ 인증번호 생성 성공 | " + email + " | " + code);

            emailService.sendAuthCode(email, code);
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("발송 실패");
        }
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Boolean> verifyEmailCode(@RequestParam String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("emailAuthCode");
        LocalDateTime sentTime = (LocalDateTime) session.getAttribute("emailAuthTime");

        boolean valid = savedCode != null
                && sentTime != null
                && savedCode.equals(code.trim())
                && LocalDateTime.now().isBefore(sentTime.plusMinutes(CODE_VALID_MINUTES));

        if (valid) {
            // 인증 성공 표시만 남기고 코드 자체는 재사용 못 하도록 제거
            session.setAttribute("emailAuthVerified", true);
            session.removeAttribute("emailAuthCode");
        }

        return ResponseEntity.ok(valid);
    }
}