package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailService emailService;
    private static final int MAX_FAILURE_COUNT = 5;
    private static final int CODE_VALID_MINUTES = 5;

    private final ConcurrentHashMap<String, VerificationCode> codeStore = new ConcurrentHashMap<>();

    public void sendCode(String email) {
        String code = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        codeStore.put(normalize(email), new VerificationCode(
                code,
                LocalDateTime.now().plusMinutes(CODE_VALID_MINUTES)
        ));
        emailService.sendAuthCode(email, code);
    }

    public boolean verifyCode(String email, String authCode) {
        String key = normalize(email);
        VerificationCode saved = codeStore.get(key);
        if (saved == null || saved.expiresAt.isBefore(LocalDateTime.now())) {
            codeStore.remove(key);
            return false;
        }

        if (saved.failureCount >= MAX_FAILURE_COUNT) {
            codeStore.remove(key);
            return false;
        }

        if (!saved.code.equals(authCode)) {
            saved.failureCount++;
            return false;
        }

        codeStore.remove(key);
        return true;
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static class VerificationCode {
        private final String code;
        private final LocalDateTime expiresAt;
        private int failureCount;

        private VerificationCode(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }
}
