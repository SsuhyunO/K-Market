package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAuthCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[K-Market] 이메일 인증번호 안내");
            message.setText("요청하신 인증번호는 [" + code + "] 입니다.\n\n인증번호를 화면에 입력해 주세요.");

            mailSender.send(message);
            System.out.println("✅ 이메일 발송 성공 → " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ 이메일 발송 실패");
            System.err.println("받는 사람: " + toEmail);
            e.printStackTrace();
            throw e;  // 컨트롤러에서 catch 하도록
        }
    }
}

/*
 만약 메일 제목/본문의 한글이 깨져서 온다면 로직 문제가 아니라
 application.properties(또는 application.yml)에 아래 설정이 빠졌을 가능성이 큽니다.

 spring.mail.default-encoding=UTF-8

 그리고 gmail 사용 시 아래 항목도 확인하세요.
 spring.mail.host=smtp.gmail.com
 spring.mail.port=587
 spring.mail.username=본인이메일
 spring.mail.password=앱비밀번호(일반 비밀번호 아님, 구글 계정 앱 비밀번호 발급 필요)
 spring.mail.properties.mail.smtp.auth=true
 spring.mail.properties.mail.smtp.starttls.enable=true
*/
