package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailService emailService;
    private final ConcurrentHashMap<String, String> codeStore = new ConcurrentHashMap<>();

    public void sendCode(String email) {
        String code = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        codeStore.put(email, code);
        emailService.sendAuthCode(email, code);
    }

    public boolean verifyCode(String email, String authCode) {
        String saved = codeStore.get(email);
        boolean ok = saved != null && saved.equals(authCode);
        if (ok) codeStore.remove(email);
        return ok;
    }
}
